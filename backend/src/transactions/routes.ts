import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const transactionFilterSchema = z.object({
  page: z.coerce.number().int().positive().default(1),
  limit: z.coerce.number().int().positive().max(100).default(50),
  type: z.enum(['DEBIT', 'CREDIT', 'TRANSFER']).optional(),
  category: z.string().uuid().optional(),
  account: z.string().uuid().optional(),
  startDate: z.string().datetime().optional(),
  endDate: z.string().datetime().optional(),
  minAmount: z.coerce.number().optional(),
  maxAmount: z.coerce.number().optional(),
  search: z.string().optional(),
  sort: z.enum(['date', 'amount', 'merchant']).default('date'),
  order: z.enum(['asc', 'desc']).default('desc'),
});

const updateTransactionSchema = z.object({
  categoryId: z.string().uuid().optional().nullable(),
  subcategory: z.string().optional().nullable(),
  merchantName: z.string().optional().nullable(),
  description: z.string().optional().nullable(),
  notes: z.string().optional().nullable(),
  tags: z.array(z.string()).optional(),
  isTransfer: z.boolean().optional(),
  isRecurring: z.boolean().optional(),
});

const bulkUpdateSchema = z.object({
  ids: z.array(z.string().uuid()).min(1).max(100),
  categoryId: z.string().uuid().optional().nullable(),
  notes: z.string().optional().nullable(),
  isTransfer: z.boolean().optional(),
});

// GET /api/v1/transactions
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const filter = transactionFilterSchema.parse(req.query);
  const skip = (filter.page - 1) * filter.limit;

  const where: any = { userId: req.user!.id };

  if (filter.type) where.type = filter.type;
  if (filter.category) where.categoryId = filter.category;
  if (filter.account) where.accountId = filter.account;
  if (filter.startDate || filter.endDate) {
    where.transactionDate = {};
    if (filter.startDate) where.transactionDate.gte = new Date(filter.startDate);
    if (filter.endDate) where.transactionDate.lte = new Date(filter.endDate);
  }
  if (filter.minAmount !== undefined || filter.maxAmount !== undefined) {
    where.amount = {};
    if (filter.minAmount !== undefined) where.amount.gte = filter.minAmount;
    if (filter.maxAmount !== undefined) where.amount.lte = filter.maxAmount;
  }
  if (filter.search) {
    where.OR = [
      { merchantName: { contains: filter.search, mode: 'insensitive' } },
      { description: { contains: filter.search, mode: 'insensitive' } },
    ];
  }

  const orderBy: any = {};
  const sortField = filter.sort === 'merchant' ? 'merchantName' : filter.sort === 'date' ? 'transactionDate' : filter.sort;
  orderBy[sortField] = filter.order;

  const [transactions, total] = await Promise.all([
    prisma.transaction.findMany({
      where,
      skip,
      take: filter.limit,
      orderBy,
      include: {
        account: {
          include: {
            institution: { select: { id: true, name: true, logo: true } },
          },
        },
        category: true,
      },
    }),
    prisma.transaction.count({ where }),
  ]);

  res.json({
    success: true,
    data: {
      items: transactions.map(t => ({
        ...t,
        amount: Number(t.amount),
        account: t.account ? {
          id: t.account.id,
          maskedNumber: t.account.maskedNumber,
          institutionName: t.account.institution?.name ?? null,
          institutionLogo: t.account.institution?.logo ?? null,
          accountType: t.account.accountType,
        } : null,
      })),
      page: filter.page,
      limit: filter.limit,
      total,
      totalPages: Math.ceil(total / filter.limit),
      hasNext: skip + transactions.length < total,
      hasPrevious: filter.page > 1,
    },
  });
}));

// GET /api/v1/transactions/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const transaction = await prisma.transaction.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
    include: {
      account: {
        include: {
          institution: { select: { id: true, name: true, logo: true } },
        },
      },
      category: true,
    },
  });

  if (!transaction) {
    throw new AppError(404, 'Transaction not found');
  }

  res.json({
    success: true,
    data: {
      ...transaction,
      amount: Number(transaction.amount),
    },
  });
}));

// PATCH /api/v1/transactions/:id
router.patch('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const data = updateTransactionSchema.parse(req.body);

  const transaction = await prisma.transaction.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!transaction) {
    throw new AppError(404, 'Transaction not found');
  }

  const updated = await prisma.transaction.update({
    where: { id: req.params.id },
    data: {
      categoryId: data.categoryId,
      subcategory: data.subcategory,
      merchantName: data.merchantName,
      description: data.description,
      notes: data.notes,
      tags: data.tags,
      isTransfer: data.isTransfer,
      isRecurring: data.isRecurring,
    },
    include: {
      account: {
        select: {
          id: true,
          maskedNumber: true,
          institutionName: true,
          accountType: true,
        },
      },
      category: true,
    },
  });

  res.json({
    success: true,
    data: {
      ...updated,
      amount: Number(updated.amount),
    },
  });
}));

// DELETE /api/v1/transactions/:id
router.delete('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const transaction = await prisma.transaction.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!transaction) {
    throw new AppError(404, 'Transaction not found');
  }

  await prisma.transaction.delete({
    where: { id: req.params.id },
  });

  res.json({ success: true, data: null });
}));

// POST /api/v1/transactions/bulk-update
router.post('/bulk-update', asyncHandler(async (req: AuthRequest, res) => {
  const data = bulkUpdateSchema.parse(req.body);

  const transactions = await prisma.transaction.findMany({
    where: { id: { in: data.ids }, userId: req.user!.id },
  });

  if (transactions.length !== data.ids.length) {
    throw new AppError(404, 'Some transactions not found');
  }

  const updated = await prisma.transaction.updateMany({
    where: { id: { in: data.ids } },
    data: {
      categoryId: data.categoryId,
      notes: data.notes,
      isTransfer: data.isTransfer,
    },
  });

  res.json({
    success: true,
    data: { updatedCount: updated.count },
  });
}));

export { router as transactionRouter };