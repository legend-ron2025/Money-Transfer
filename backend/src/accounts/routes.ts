import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const syncAccountSchema = z.object({
  accountId: z.string().uuid().optional(),
  consentId: z.string().uuid().optional(),
  fullSync: z.boolean().default(false),
});

// GET /api/v1/accounts
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const accounts = await prisma.account.findMany({
    where: { userId: req.user!.id },
    include: {
      institution: true,
    },
    orderBy: { createdAt: 'desc' },
  });

  res.json({
    success: true,
    data: accounts.map(acc => ({
      ...acc,
      balance: Number(acc.balance),
      availableBalance: acc.availableBalance ? Number(acc.availableBalance) : null,
      creditLimit: acc.creditLimit ? Number(acc.creditLimit) : null,
      institution: acc.institution ? {
        id: acc.institution.id,
        name: acc.institution.name,
        logo: acc.institution.logo,
        type: acc.institution.type,
        country: acc.institution.country,
        supportedFeatures: [],
      } : null,
    })),
  });
}));

// GET /api/v1/accounts/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const account = await prisma.account.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
    include: { institution: true },
  });

  if (!account) {
    throw new AppError(404, 'Account not found');
  }

  res.json({
    success: true,
    data: {
      ...account,
      balance: Number(account.balance),
      availableBalance: account.availableBalance ? Number(account.availableBalance) : null,
      creditLimit: account.creditLimit ? Number(account.creditLimit) : null,
    },
  });
}));

// POST /api/v1/accounts/sync
router.post('/sync', asyncHandler(async (req: AuthRequest, res) => {
  const data = syncAccountSchema.parse(req.body);

  if (data.accountId) {
    // Sync specific account
    const account = await prisma.account.findFirst({
      where: { id: data.accountId, userId: req.user!.id },
    });

    if (!account) {
      throw new AppError(404, 'Account not found');
    }

    // In production, trigger actual sync with AA/bank
    // For now, simulate sync
    await prisma.account.update({
      where: { id: account.id },
      data: {
        syncStatus: 'SYNCING',
      },
    });

    // Simulate async sync
    setTimeout(async () => {
      await prisma.account.update({
        where: { id: account.id },
        data: {
          syncStatus: 'SYNCED',
          lastSyncedAt: new Date(),
          lastSuccessfulSync: new Date(),
        },
      });
    }, 2000);

    res.json({
      success: true,
      data: {
        syncedTransactions: 0,
        newTransactions: 0,
        updatedTransactions: 0,
        failedTransactions: 0,
        lastSyncTimestamp: new Date().toISOString(),
        nextSyncTimestamp: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
      },
    });
  } else {
    // Sync all accounts
    const accounts = await prisma.account.findMany({
      where: { userId: req.user!.id, isActive: true },
    });

    for (const account of accounts) {
      await prisma.account.update({
        where: { id: account.id },
        data: { syncStatus: 'SYNCING' },
      });
    }

    // Simulate async sync
    setTimeout(async () => {
      for (const account of accounts) {
        await prisma.account.update({
          where: { id: account.id },
          data: {
            syncStatus: 'SYNCED',
            lastSyncedAt: new Date(),
            lastSuccessfulSync: new Date(),
          },
        });
      }
    }, 3000);

    res.json({
      success: true,
      data: {
        syncedTransactions: 0,
        newTransactions: 0,
        updatedTransactions: 0,
        failedTransactions: 0,
        lastSyncTimestamp: new Date().toISOString(),
        nextSyncTimestamp: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
      },
    });
  }
}));

// POST /api/v1/accounts/:id/sync
router.post('/:id/sync', asyncHandler(async (req: AuthRequest, res) => {
  const account = await prisma.account.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!account) {
    throw new AppError(404, 'Account not found');
  }

  await prisma.account.update({
    where: { id: account.id },
    data: { syncStatus: 'SYNCING' },
  });

  setTimeout(async () => {
    await prisma.account.update({
      where: { id: account.id },
      data: {
        syncStatus: 'SYNCED',
        lastSyncedAt: new Date(),
        lastSuccessfulSync: new Date(),
      },
    });
  }, 2000);

  res.json({
    success: true,
    data: {
      syncedTransactions: 0,
      newTransactions: 0,
      updatedTransactions: 0,
      failedTransactions: 0,
      lastSyncTimestamp: new Date().toISOString(),
      nextSyncTimestamp: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
    },
  });
}));

// POST /api/v1/accounts/:id/reconnect
router.post('/:id/reconnect', asyncHandler(async (req: AuthRequest, res) => {
  const account = await prisma.account.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!account) {
    throw new AppError(404, 'Account not found');
  }

  // Trigger reconnection flow
  await prisma.account.update({
    where: { id: account.id },
    data: { syncStatus: 'PENDING' },
  });

  res.json({
    success: true,
    data: {
      ...account,
      syncStatus: 'PENDING',
    },
  });
}));

// POST /api/v1/accounts/:id/disconnect
router.post('/:id/disconnect', asyncHandler(async (req: AuthRequest, res) => {
  const account = await prisma.account.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!account) {
    throw new AppError(404, 'Account not found');
  }

  await prisma.account.update({
    where: { id: account.id },
    data: {
      isActive: false,
      syncStatus: 'DISCONNECTED',
    },
  });

  res.json({ success: true, data: null });
}));

export { router as accountRouter };