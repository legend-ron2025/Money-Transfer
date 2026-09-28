import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const createBudgetSchema = z.object({
  categoryId: z.string().uuid(),
  amount: z.number().positive(),
  period: z.enum(['WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY', 'CUSTOM']),
  startDate: z.string().datetime(),
  endDate: z.string().datetime().optional().nullable(),
  alertThreshold: z.number().min(0).max(1).default(0.8),
});

const updateBudgetSchema = z.object({
  amount: z.number().positive().optional(),
  period: z.enum(['WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY', 'CUSTOM']).optional(),
  startDate: z.string().datetime().optional(),
  endDate: z.string().datetime().optional().nullable(),
  alertThreshold: z.number().min(0).max(1).optional(),
  isActive: z.boolean().optional(),
});

// GET /api/v1/budgets
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const budgets = await prisma.budget.findMany({
    where: { userId: req.user!.id },
    include: { category: true },
    orderBy: { createdAt: 'desc' },
  });

  res.json({
    success: true,
    data: budgets.map(b => ({
      ...b,
      amount: Number(b.amount),
      spentAmount: Number(b.spentAmount),
    })),
  });
}));

// GET /api/v1/budgets/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const budget = await prisma.budget.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
    include: { category: true },
  });

  if (!budget) {
    throw new AppError(404, 'Budget not found');
  }

  res.json({
    success: true,
    data: {
      ...budget,
      amount: Number(budget.amount),
      spentAmount: Number(budget.spentAmount),
    },
  });
}));

// POST /api/v1/budgets
router.post('/', asyncHandler(async (req: AuthRequest, res) => {
  const data = createBudgetSchema.parse(req.body);

  // Verify category exists and is expense type
  const category = await prisma.category.findFirst({
    where: {
      id: data.categoryId,
      OR: [{ userId: null }, { userId: req.user!.id }],
      type: 'EXPENSE',
    },
  });

  if (!category) {
    throw new AppError(404, 'Category not found or not an expense category');
  }

  // Check for existing active budget for same category and period
  const existing = await prisma.budget.findFirst({
    where: {
      userId: req.user!.id,
      categoryId: data.categoryId,
      period: data.period,
      isActive: true,
      startDate: { lte: new Date(data.startDate) },
      OR: [
        { endDate: null },
        { endDate: { gte: new Date(data.startDate) } },
      ],
    },
  });

  if (existing) {
    throw new AppError(409, 'Active budget already exists for this category and period');
  }

  const budget = await prisma.budget.create({
    data: {
      ...data,
      userId: req.user!.id,
      startDate: new Date(data.startDate),
      endDate: data.endDate ? new Date(data.endDate) : null,
    },
    include: { category: true },
  });

  res.status(201).json({
    success: true,
    data: {
      ...budget,
      amount: Number(budget.amount),
      spentAmount: Number(budget.spentAmount),
    },
  });
}));

// PATCH /api/v1/budgets/:id
router.patch('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const data = updateBudgetSchema.parse(req.body);

  const budget = await prisma.budget.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!budget) {
    throw new AppError(404, 'Budget not found');
  }

  const updated = await prisma.budget.update({
    where: { id: req.params.id },
    data: {
      amount: data.amount,
      period: data.period,
      startDate: data.startDate ? new Date(data.startDate) : undefined,
      endDate: data.endDate ? new Date(data.endDate) : data.endDate === '' ? null : undefined,
      alertThreshold: data.alertThreshold,
      isActive: data.isActive,
    },
    include: { category: true },
  });

  res.json({
    success: true,
    data: {
      ...updated,
      amount: Number(updated.amount),
      spentAmount: Number(updated.spentAmount),
    },
  });
}));

// DELETE /api/v1/budgets/:id
router.delete('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const budget = await prisma.budget.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!budget) {
    throw new AppError(404, 'Budget not found');
  }

  await prisma.budget.delete({ where: { id: req.params.id } });

  res.json({ success: true, data: null });
}));

// GET /api/v1/budgets/status
router.get('/status', asyncHandler(async (req: AuthRequest, res) => {
  const budgets = await prisma.budget.findMany({
    where: { userId: req.user!.id, isActive: true },
    include: { category: true },
  });

  const now = new Date();
  const budgetStatuses = budgets
    .filter(b => {
      const start = new Date(b.startDate);
      const end = b.endDate ? new Date(b.endDate) : new Date('2099-12-31');
      return now >= start && now <= end;
    })
    .map(b => {
      const spent = Number(b.spentAmount);
      const amount = Number(b.amount);
      const percentage = amount > 0 ? (spent / amount) * 100 : 0;
      let status = 'NORMAL';
      if (percentage >= 100) status = 'EXCEEDED';
      else if (percentage >= 90) status = 'NEAR_LIMIT';
      else if (percentage >= 70) status = 'WATCH';

      return {
        budgetId: b.id,
        categoryId: b.categoryId,
        categoryName: b.category.name,
        budgetAmount: amount,
        spentAmount: spent,
        remainingAmount: amount - spent,
        percentageUsed: percentage,
        status,
        periodStart: b.startDate.toISOString(),
        periodEnd: b.endDate?.toISOString() || null,
      };
    });

  res.json({ success: true, data: budgetStatuses });
}));

export { router as budgetRouter };