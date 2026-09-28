import { Worker, Job } from 'bullmq';
import { Redis } from 'ioredis';
import { prisma } from '../index.js';
import { createDataSession, fetchFIData } from '../integrations/aa/setuAAClient.js';
import { parseFIPayload, mapFITransactions } from '../integrations/aa/fiDataMapper.js';
import { decryptOrParsePayload } from '../integrations/aa/jweDecryptor.js';
import { processTransactions } from '../integrations/transactionEngine/pipeline.js';
import { analyticsQueue, notificationsQueue } from '../queues/index.js';
import type { AADataSyncJob } from '../queues/index.js';

const conn = new Redis(process.env.REDIS_URL || 'redis://localhost:6379', {
  maxRetriesPerRequest: null,
  enableReadyCheck: false,
});

export const aaDataSyncWorker = new Worker<AADataSyncJob>(
  'aa-data-sync',
  async (job: Job<AADataSyncJob>) => {
    const { userId, consentId, dateRange } = job.data;
    console.log(`[AASync] Starting fetch for user ${userId}, consent ${consentId}`);

    // 1. Create a data session
    const session = await createDataSession(consentId, {
      from: dateRange?.from ?? new Date(Date.now() - 180 * 86_400_000).toISOString(),
      to:   dateRange?.to   ?? new Date().toISOString(),
    });

    await job.updateProgress(20);

    // 2. Poll / await FI data (simplified — real impl would poll or use webhook)
    const rawData = await fetchFIData(session.sessionId ?? session.id);
    await job.updateProgress(50);

    // 3. Decrypt JWE payload
    const payload   = await decryptOrParsePayload(typeof rawData === 'string' ? rawData : JSON.stringify(rawData));
    const fiAccounts = parseFIPayload(payload);
    await job.updateProgress(65);

    // 4. Map FI accounts to user accounts (match by masked number)
    const userAccounts = await prisma.account.findMany({ where: { userId }, select: { id: true, maskedNumber: true } });
    let totalInserted = 0;

    for (const fiAcc of fiAccounts) {
      // Find matching account by last 4 digits
      const last4   = fiAcc.maskedAccountNumber.slice(-4);
      const account = userAccounts.find(a => a.maskedNumber.endsWith(last4)) ?? userAccounts[0];
      if (!account) continue;

      const raws = mapFITransactions(fiAcc, account.id, userId);
      const result = await processTransactions(raws);
      totalInserted += result.inserted;
      console.log(`[AASync] Account ${account.id}: inserted=${result.inserted} dupes=${result.duplicates}`);
    }

    await job.updateProgress(90);

    // 5. Invalidate analytics cache
    if (totalInserted > 0) {
      await analyticsQueue.add('aa-sync', { userId, invalidateAll: true });
    }

    // 6. Send "sync complete" push notification
    await notificationsQueue.add('aa-sync-complete', {
      userId,
      type:  'SYNC_COMPLETE',
      title: 'Bank sync complete',
      body:  totalInserted > 0
        ? `${totalInserted} new transaction${totalInserted > 1 ? 's' : ''} imported from your bank.`
        : 'Your accounts are up to date.',
    });

    await job.updateProgress(100);
    return { inserted: totalInserted };
  },
  { connection: conn, concurrency: 3 },
);

aaDataSyncWorker.on('failed', (job, err) =>
  console.error(`[AASync] job ${job?.id} failed:`, err.message),
);
