import { Worker, Job } from 'bullmq';
import { Redis } from 'ioredis';
import { prisma } from '../index.js';
import type { AnalyticsJob } from '../queues/index.js';

const conn = new Redis(process.env.REDIS_URL || 'redis://localhost:6379', {
  maxRetriesPerRequest: null,
  enableReadyCheck: false,
});

export const analyticsWorker = new Worker<AnalyticsJob>(
  'analytics',
  async (job: Job<AnalyticsJob>) => {
    const { userId, invalidateAll, period } = job.data;

    if (invalidateAll) {
      await prisma.analyticsCache.deleteMany({ where: { userId } });
    } else {
      // Expire current-period caches so next read recomputes them
      const keysToDelete = period
        ? [`overview_${period}`, `spending_${period}`, `income_${period}`]
        : ['overview_month', 'overview_week', 'overview_today'];

      await prisma.analyticsCache.deleteMany({
        where: { userId, cacheKey: { in: keysToDelete } },
      });
    }
  },
  { connection: conn, concurrency: 15 },
);

analyticsWorker.on('failed', (job, err) =>
  console.error(`[AnalyticsWorker] job ${job?.id} failed:`, err.message),
);
