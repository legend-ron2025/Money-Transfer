import { Queue } from 'bullmq';
import { Redis } from 'ioredis';

const REDIS_URL = process.env.REDIS_URL || 'redis://localhost:6379';

function makeRedis() {
  return new Redis(REDIS_URL, {
    maxRetriesPerRequest: null, // REQUIRED by BullMQ
    enableReadyCheck: false,
    lazyConnect: true,
  });
}

const DEFAULT_JOB_OPTIONS = {
  attempts: 3,
  backoff: { type: 'exponential' as const, delay: 3000 },
  removeOnComplete: { count: 200 },
  removeOnFail:     { count: 100 },
};

export const analyticsQueue = new Queue('analytics', {
  connection: makeRedis(),
  defaultJobOptions: DEFAULT_JOB_OPTIONS,
});

export const notificationsQueue = new Queue('notifications', {
  connection: makeRedis(),
  defaultJobOptions: { ...DEFAULT_JOB_OPTIONS, attempts: 5 },
});

export const aaDataSyncQueue = new Queue('aa-data-sync', {
  connection: makeRedis(),
  defaultJobOptions: { ...DEFAULT_JOB_OPTIONS, attempts: 3 },
});

export const transactionProcessingQueue = new Queue('transaction-processing', {
  connection: makeRedis(),
  defaultJobOptions: { ...DEFAULT_JOB_OPTIONS, removeOnComplete: { count: 500 } },
});

export const subscriptionDetectionQueue = new Queue('subscription-detection', {
  connection: makeRedis(),
  defaultJobOptions: { attempts: 2, removeOnComplete: { count: 50 }, removeOnFail: { count: 20 } },
});

// ── Job type interfaces ───────────────────────────────────────────────────────

export interface AnalyticsJob {
  userId: string;
  invalidateAll?: boolean;
  period?: string;
}

export interface NotificationJob {
  userId:       string;
  type:         string;
  title:        string;
  body:         string;
  data?:        Record<string, string>;
  deviceTokens?: string[];
}

export interface AADataSyncJob {
  userId:     string;
  consentId:  string;
  accountIds: string[];
  dateRange?: { from: string; to: string };
}

export interface TransactionProcessingJob {
  userId: string;
  transactions: Array<{
    externalId?:     string;
    amount:          number;
    type?:           string;
    description:     string;
    transactionDate: string;
    upiReference?:   string;
    bankReference?:  string;
    source:          string;
    accountId:       string;
  }>;
}
