import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index.js';
import { AuthRequest } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import { analyticsQueue } from '../queues/index.js';

const router = Router();

// ── GET /api/v1/sync/delta?since=<ISO> ────────────────────────────────────────
router.get('/delta', asyncHandler(async (req: AuthRequest, res) => {
  const { since } = z.object({
    since: z.string().datetime().optional(),
  }).parse(req.query);

  const userId    = req.user!.id;
  const sinceDate = since
    ? new Date(since)
    : new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);

  const [transactions, accounts, categories, budgets, goals, deletedRecords] =
    await Promise.all([
      prisma.transaction.findMany({
        where:   { userId, updatedAt: { gt: sinceDate } },
        include: {
          account:  { include: { institution: { select: { name: true, logo: true } } } },
          category: true,
        },
        orderBy: { updatedAt: 'desc' },
        take:    500,
      }),
      prisma.account.findMany({
        where:   { userId, updatedAt: { gt: sinceDate } },
        include: { institution: true },
      }),
      prisma.category.findMany({
        where: {
          updatedAt: { gt: sinceDate },
          OR: [{ userId: null }, { userId }],
        },
      }),
      prisma.budget.findMany({
        where:   { userId, updatedAt: { gt: sinceDate } },
        include: { category: true },
      }),
      prisma.goal.findMany({
        where: { userId, updatedAt: { gt: sinceDate } },
      }),
      prisma.deletedRecord.findMany({
        where: { userId, deletedAt: { gt: sinceDate } },
      }),
    ]);

  const nextSyncToken = new Date().toISOString();

  res.json({
    success: true,
    data: {
      transactions: transactions.map(t => ({
        ...t,
        amount:           Number(t.amount),
        account: t.account ? {
          id:              t.account.id,
          maskedNumber:    t.account.maskedNumber,
          institutionName: t.account.institution?.name,
          institutionLogo: t.account.institution?.logo,
          accountType:     t.account.accountType,
        } : null,
      })),
      accounts: accounts.map(a => ({
        ...a,
        balance:          Number(a.balance),
        availableBalance: a.availableBalance ? Number(a.availableBalance) : null,
        creditLimit:      a.creditLimit ? Number(a.creditLimit) : null,
      })),
      categories,
      budgets: budgets.map(b => ({
        ...b,
        amount:      Number(b.amount),
        spentAmount: Number(b.spentAmount),
      })),
      goals: goals.map(g => ({
        ...g,
        targetAmount:        Number(g.targetAmount),
        currentAmount:       Number(g.currentAmount),
        monthlyContribution: g.monthlyContribution ? Number(g.monthlyContribution) : null,
      })),
      deletedIds: deletedRecords.map(r => ({
        entityType: r.entityType,
        entityId:   r.entityId,
      })),
      nextSyncToken,
      serverTime: nextSyncToken,
    },
  });
}));

// ── POST /api/v1/sync/push ────────────────────────────────────────────────────
router.post('/push', asyncHandler(async (req: AuthRequest, res) => {
  const schema = z.object({
    transactions: z.array(z.object({
      id:              z.string().uuid(),
      accountId:       z.string().uuid(),
      amount:          z.number().positive(),
      type:            z.enum(['DEBIT', 'CREDIT', 'TRANSFER']),
      description:     z.string().min(1).max(500),
      transactionDate: z.string().datetime(),
      categoryId:      z.string().uuid().optional().nullable(),
      notes:           z.string().max(500).optional().nullable(),
      tags:            z.array(z.string()).optional(),
      source:          z.string().default('MANUAL'),
      updatedAt:       z.string().datetime(),
    })).optional().default([]),
    budgets: z.array(z.object({
      id:        z.string().uuid(),
      categoryId:z.string().uuid(),
      amount:    z.number().positive(),
      period:    z.string(),
      startDate: z.string().datetime(),
      updatedAt: z.string().datetime(),
    })).optional().default([]),
    goals: z.array(z.object({
      id:            z.string().uuid(),
      name:          z.string().min(1).max(100),
      targetAmount:  z.number().positive(),
      currentAmount: z.number().min(0),
      targetDate:    z.string().datetime(),
      updatedAt:     z.string().datetime(),
    })).optional().default([]),
  });

  const data    = schema.parse(req.body);
  const userId  = req.user!.id;
  let inserted  = 0;
  let updated   = 0;
  const errors: string[] = [];

  // Transactions
  for (const txn of data.transactions) {
    try {
      const existing = await prisma.transaction.findFirst({
        where: { id: txn.id, userId },
        select: { id: true, source: true },
      });

      if (existing) {
        // Client wins for MANUAL-sourced fields (notes, category, tags)
        await prisma.transaction.update({
          where: { id: txn.id },
          data: {
            categoryId: txn.categoryId ?? undefined,
            notes:      txn.notes ?? undefined,
            tags:       txn.tags  ?? undefined,
            updatedAt:  new Date(),
          },
        });
        updated++;
      } else if (txn.source === 'MANUAL') {
        // Verify the account belongs to this user
        const account = await prisma.account.findFirst({
          where: { id: txn.accountId, userId },
          select: { id: true },
        });
        if (!account) { errors.push(`Account ${txn.accountId} not found`); continue; }

        await prisma.transaction.create({
          data: {
            id:             txn.id,
            userId,
            accountId:      txn.accountId,
            amount:         txn.amount,
            currency:       'INR',
            type:           txn.type,
            status:         'COMPLETED',
            description:    txn.description,
            transactionDate:new Date(txn.transactionDate),
            categoryId:     txn.categoryId ?? null,
            notes:          txn.notes ?? null,
            tags:           txn.tags ?? [],
            source:         'MANUAL',
            confidence:     1.0,
          },
        });
        inserted++;
      }
    } catch (err) {
      errors.push(err instanceof Error ? err.message : String(err));
    }
  }

  if (inserted + updated > 0) {
    await analyticsQueue.add('sync-push', { userId, invalidateAll: true });
  }

  res.json({
    success: true,
    data: { inserted, updated, errors, serverTime: new Date().toISOString() },
  });
}));

export { router as syncRouter };
