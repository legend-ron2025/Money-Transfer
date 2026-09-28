import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import morgan from 'morgan';
import rateLimit from 'express-rate-limit';
import { PrismaClient } from '@prisma/client';
import { authRouter }         from './auth/routes.js';
import { userRouter }         from './users/routes.js';
import { accountRouter }      from './accounts/routes.js';
import { transactionRouter }  from './transactions/routes.js';
import { categoryRouter }     from './categories/routes.js';
import { budgetRouter }       from './budgets/routes.js';
import { goalRouter }         from './goals/routes.js';
import { analyticsRouter }    from './analytics/routes.js';
import { consentRouter }      from './consent/routes.js';
import { notificationRouter } from './notifications/routes.js';
import { syncRouter }         from './sync/routes.js';
import { consentWebhookRouter } from './integrations/aa/consentWebhook.js';
import { errorHandler }       from './middleware/errorHandler.js';
import { authMiddleware }     from './middleware/auth.js';
import { auditMiddleware, sanitiseBody, securityHeaders } from './middleware/security.js';

// Importing the workers starts them automatically
import './workers/analyticsWorker.js';
import './workers/notificationsWorker.js';
import './workers/transactionProcessingWorker.js';
import './workers/aaDataSyncWorker.js';

import {
  analyticsQueue,
  notificationsQueue,
  aaDataSyncQueue,
  transactionProcessingQueue,
  subscriptionDetectionQueue,
} from './queues/index.js';

const app    = express();
export const prisma = new PrismaClient();

// ── Core middleware ──────────────────────────────────────────────────────────
app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors({
  origin:      process.env.CORS_ORIGIN?.split(',') ?? ['http://localhost:3000', 'http://localhost:3001'],
  credentials: true,
}));
app.use(compression());
app.use(morgan('combined'));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(securityHeaders);
app.use(sanitiseBody);
app.use(auditMiddleware as any);

// ── Rate limiting ────────────────────────────────────────────────────────────
app.use('/api/', rateLimit({
  windowMs: 15 * 60 * 1000,
  max:      300,
  message:  { error: 'Too many requests, please try again later' },
}));
const authLimiter = rateLimit({ windowMs: 15 * 60 * 1000, max: 10, message: { error: 'Too many attempts' } });
app.use('/api/v1/auth/login',    authLimiter);
app.use('/api/v1/auth/register', authLimiter);

// ── Health check ─────────────────────────────────────────────────────────────
app.get('/health', (_req, res) =>
  res.json({ status: 'ok', timestamp: new Date().toISOString(), version: '1.0.0' }),
);

// ── Bull Board (queue monitoring UI) ────────────────────────────────────────
(async () => {
  try {
    const { createBullBoard }  = await import('@bull-board/express') as any;
    const { BullMQAdapter }    = await import('@bull-board/express') as any;
    const { ExpressAdapter }   = await import('@bull-board/express') as any;
    const serverAdapter = new ExpressAdapter();
    serverAdapter.setBasePath('/admin/queues');
    createBullBoard({
      queues: [
        new BullMQAdapter(analyticsQueue),
        new BullMQAdapter(notificationsQueue),
        new BullMQAdapter(aaDataSyncQueue),
        new BullMQAdapter(transactionProcessingQueue),
        new BullMQAdapter(subscriptionDetectionQueue),
      ],
      serverAdapter,
    });
    app.use('/admin/queues', serverAdapter.getRouter());
    console.log('📊 Bull Board: /admin/queues');
  } catch {
    // @bull-board not installed — skip
  }
})();

// ── API routes ───────────────────────────────────────────────────────────────
app.use('/api/v1/auth',          authRouter);
app.use('/api/v1/users',         authMiddleware, userRouter);
app.use('/api/v1/accounts',      authMiddleware, accountRouter);
app.use('/api/v1/transactions',  authMiddleware, transactionRouter);
app.use('/api/v1/categories',    authMiddleware, categoryRouter);
app.use('/api/v1/budgets',       authMiddleware, budgetRouter);
app.use('/api/v1/goals',         authMiddleware, goalRouter);
app.use('/api/v1/analytics',     authMiddleware, analyticsRouter);
app.use('/api/v1/consents',      authMiddleware, consentRouter);
app.use('/api/v1/notifications', authMiddleware, notificationRouter);
app.use('/api/v1/sync',          authMiddleware, syncRouter);
// AA consent webhook — public (no auth), Setu calls it directly
app.use('/api/v1/consents/webhook', consentWebhookRouter);

// ── Error handling ───────────────────────────────────────────────────────────
app.use(errorHandler);
app.use((_req, res) => res.status(404).json({ error: 'Not found' }));

// ── Start ────────────────────────────────────────────────────────────────────
const PORT = process.env.PORT ?? 3000;
app.listen(PORT, () => {
  console.log(`🚀 MoneyTracker API on port ${PORT}`);
  console.log(`🏥 Health: http://localhost:${PORT}/health`);
});

const shutdown = async () => { await prisma.$disconnect(); process.exit(0); };
process.on('SIGTERM', shutdown);
process.on('SIGINT',  shutdown);
