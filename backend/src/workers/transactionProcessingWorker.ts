import { Worker, Job } from 'bullmq';
import { Redis } from 'ioredis';
import { processTransactions } from '../integrations/transactionEngine/pipeline.js';
import { analyticsQueue } from '../queues/index.js';
import type { TransactionProcessingJob } from '../queues/index.js';

const conn = new Redis(process.env.REDIS_URL || 'redis://localhost:6379', {
  maxRetriesPerRequest: null,
  enableReadyCheck: false,
});

export const transactionProcessingWorker = new Worker<TransactionProcessingJob>(
  'transaction-processing',
  async (job: Job<TransactionProcessingJob>) => {
    const { userId, transactions } = job.data;
    console.log(`[TxnWorker] processing ${transactions.length} txns for ${userId}`);

    const result = await processTransactions(
      transactions.map(t => ({ ...t, userId })),
    );

    console.log(`[TxnWorker] inserted=${result.inserted} dupes=${result.duplicates} errors=${result.errors.length}`);

    if (result.inserted > 0) {
      await analyticsQueue.add('invalidate', { userId, invalidateAll: true });
    }

    if (result.errors.length > 0) {
      console.error('[TxnWorker] errors:', result.errors);
    }

    return result;
  },
  { connection: conn, concurrency: 5 },
);

transactionProcessingWorker.on('failed', (job, err) =>
  console.error(`[TxnWorker] job ${job?.id} failed:`, err.message),
);
