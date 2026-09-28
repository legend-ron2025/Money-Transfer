import { Worker, Job } from 'bullmq';
import { Redis } from 'ioredis';
import { prisma } from '../index.js';
import type { NotificationJob } from '../queues/index.js';

const conn = new Redis(process.env.REDIS_URL || 'redis://localhost:6379', {
  maxRetriesPerRequest: null,
  enableReadyCheck: false,
});

async function sendFCM(tokens: string[], title: string, body: string, data?: Record<string, string>) {
  try {
    const { getMessaging } = await import('firebase-admin/messaging');
    const results = await Promise.allSettled(
      tokens.map(token =>
        getMessaging().send({
          token,
          notification: { title, body },
          data: data ?? {},
          android: {
            priority: 'high',
            notification: { channelId: 'money_tracker_alerts', sound: 'default' },
          },
        }),
      ),
    );
    const ok = results.filter(r => r.status === 'fulfilled').length;
    console.log(`[FCM] ${ok}/${tokens.length} delivered`);
  } catch (err) {
    console.error('[FCM] send error:', err);
  }
}

export const notificationsWorker = new Worker<NotificationJob>(
  'notifications',
  async (job: Job<NotificationJob>) => {
    const { userId, type, title, body, data, deviceTokens } = job.data;

    // Persist notification in DB
    await prisma.notification.create({
      data: {
        userId,
        type,
        title,
        body,
        data: data ? (data as any) : undefined,
        priority: ['BUDGET_ALERT', 'LARGE_TRANSACTION'].includes(type) ? 'HIGH' : 'NORMAL',
      },
    });

    // Push if tokens provided
    if (deviceTokens && deviceTokens.length > 0) {
      await sendFCM(deviceTokens, title, body, data);
    }
  },
  { connection: conn, concurrency: 20, limiter: { max: 100, duration: 1000 } },
);

notificationsWorker.on('failed', (job, err) =>
  console.error(`[NotificationsWorker] job ${job?.id} failed:`, err.message),
);
