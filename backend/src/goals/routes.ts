import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const createGoalSchema = z.object({
  name: z.string().min(1).max(100),
  description: z.string().optional().nullable(),
  targetAmount: z.number().positive(),
  currency: z.string().length(3).default('INR'),
  targetDate: z.string().datetime(),
  monthlyContribution: z.number().positive().optional().nullable(),
  icon: z.string().optional().nullable(),
  color: z.string().optional().nullable(),
});

const updateGoalSchema = z.object({
  name: z.string().min(1).max(100).optional(),
  description: z.string().optional().nullable(),
  targetAmount: z.number().positive().optional(),
  targetDate: z.string().datetime().optional(),
  monthlyContribution: z.number().positive().optional().nullable(),
  icon: z.string().optional().nullable(),
  color: z.string().optional().nullable(),
  status: z.enum(['ACTIVE', 'COMPLETED', 'PAUSED', 'CANCELLED']).optional(),
});

const contributeSchema = z.object({
  amount: z.number().positive(),
});

// GET /api/v1/goals
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const goals = await prisma.goal.findMany({
    where: { userId: req.user!.id },
    orderBy: { targetDate: 'asc' },
  });

  res.json({
    success: true,
    data: goals.map(g => ({
      ...g,
      targetAmount: Number(g.targetAmount),
      currentAmount: Number(g.currentAmount),
      monthlyContribution: g.monthlyContribution ? Number(g.monthlyContribution) : null,
    })),
  });
}));

// GET /api/v1/goals/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const goal = await prisma.goal.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!goal) {
    throw new AppError(404, 'Goal not found');
  }

  res.json({
    success: true,
    data: {
      ...goal,
      targetAmount: Number(goal.targetAmount),
      currentAmount: Number(goal.currentAmount),
      monthlyContribution: goal.monthlyContribution ? Number(goal.monthlyContribution) : null,
    },
  });
}));

// POST /api/v1/goals
router.post('/', asyncHandler(async (req: AuthRequest, res) => {
  const data = createGoalSchema.parse(req.body);

  const goal = await prisma.goal.create({
    data: {
      ...data,
      userId: req.user!.id,
      targetDate: new Date(data.targetDate),
      targetAmount: data.targetAmount,
      currentAmount: 0,
      monthlyContribution: data.monthlyContribution,
      status: 'ACTIVE',
    },
  });

  res.status(201).json({
    success: true,
    data: {
      ...goal,
      targetAmount: Number(goal.targetAmount),
      currentAmount: Number(goal.currentAmount),
      monthlyContribution: goal.monthlyContribution ? Number(goal.monthlyContribution) : null,
    },
  });
}));

// PATCH /api/v1/goals/:id
router.patch('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const data = updateGoalSchema.parse(req.body);

  const goal = await prisma.goal.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!goal) {
    throw new AppError(404, 'Goal not found');
  }

  const updated = await prisma.goal.update({
    where: { id: req.params.id },
    data: {
      name: data.name,
      description: data.description,
      targetAmount: data.targetAmount,
      targetDate: data.targetDate ? new Date(data.targetDate) : undefined,
      monthlyContribution: data.monthlyContribution,
      icon: data.icon,
      color: data.color,
      status: data.status,
    },
  });

  res.json({
    success: true,
    data: {
      ...updated,
      targetAmount: Number(updated.targetAmount),
      currentAmount: Number(updated.currentAmount),
      monthlyContribution: updated.monthlyContribution ? Number(updated.monthlyContribution) : null,
    },
  });
}));

// DELETE /api/v1/goals/:id
router.delete('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const goal = await prisma.goal.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!goal) {
    throw new AppError(404, 'Goal not found');
  }

  await prisma.goal.delete({ where: { id: req.params.id } });

  res.json({ success: true, data: null });
}));

// POST /api/v1/goals/:id/contribute
router.post('/:id/contribute', asyncHandler(async (req: AuthRequest, res) => {
  const data = contributeSchema.parse(req.body);

  const goal = await prisma.goal.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!goal) {
    throw new AppError(404, 'Goal not found');
  }

  const newAmount = Number(goal.currentAmount) + data.amount;
  const targetAmount = Number(goal.targetAmount);
  
  let status = goal.status;
  if (newAmount >= targetAmount && status === 'ACTIVE') {
    status = 'COMPLETED';
  }

  const updated = await prisma.goal.update({
    where: { id: req.params.id },
    data: {
      currentAmount: newAmount,
      status,
    },
  });

  res.json({
    success: true,
    data: {
      ...updated,
      targetAmount: Number(updated.targetAmount),
      currentAmount: Number(updated.currentAmount),
      monthlyContribution: updated.monthlyContribution ? Number(updated.monthlyContribution) : null,
    },
  });
}));

export { router as goalRouter };