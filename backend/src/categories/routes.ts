import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const createCategorySchema = z.object({
  name: z.string().min(1).max(100),
  parentId: z.string().uuid().optional().nullable(),
  icon: z.string().min(1),
  type: z.enum(['INCOME', 'EXPENSE', 'TRANSFER']),
  color: z.string().optional().nullable(),
});

const updateCategorySchema = z.object({
  name: z.string().min(1).max(100).optional(),
  icon: z.string().optional(),
  color: z.string().optional().nullable(),
  sortOrder: z.number().int().optional(),
});

// GET /api/v1/categories
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  // Get system categories + user's custom categories
  const categories = await prisma.category.findMany({
    where: {
      OR: [
        { userId: null }, // System categories
        { userId: req.user!.id }, // User's custom categories
      ],
    },
    orderBy: [{ type: 'asc' }, { sortOrder: 'asc' }, { name: 'asc' }],
  });

  res.json({
    success: true,
    data: categories,
  });
}));

// GET /api/v1/categories/tree
router.get('/tree', asyncHandler(async (req: AuthRequest, res) => {
  const categories = await prisma.category.findMany({
    where: {
      OR: [
        { userId: null },
        { userId: req.user!.id },
      ],
      parentId: null, // Only parent categories
    },
    orderBy: [{ type: 'asc' }, { sortOrder: 'asc' }, { name: 'asc' }],
    include: {
      children: {
        orderBy: { sortOrder: 'asc' },
      },
    },
  });

  res.json({
    success: true,
    data: categories,
  });
}));

// GET /api/v1/categories/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const category = await prisma.category.findFirst({
    where: {
      id: req.params.id,
      OR: [
        { userId: null },
        { userId: req.user!.id },
      ],
    },
  });

  if (!category) {
    throw new AppError(404, 'Category not found');
  }

  res.json({ success: true, data: category });
}));

// POST /api/v1/categories
router.post('/', asyncHandler(async (req: AuthRequest, res) => {
  const data = createCategorySchema.parse(req.body);

  const category = await prisma.category.create({
    data: {
      ...data,
      userId: req.user!.id,
      isSystem: false,
    },
  });

  res.status(201).json({ success: true, data: category });
}));

// PATCH /api/v1/categories/:id
router.patch('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const data = updateCategorySchema.parse(req.body);

  const category = await prisma.category.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!category) {
    throw new AppError(404, 'Category not found or not owned by user');
  }

  if (category.isSystem) {
    throw new AppError(403, 'Cannot modify system categories');
  }

  const updated = await prisma.category.update({
    where: { id: req.params.id },
    data,
  });

  res.json({ success: true, data: updated });
}));

export { router as categoryRouter };