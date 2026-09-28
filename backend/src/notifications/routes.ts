import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const updatePreferencesSchema = z.object({
  transactionAlerts: z.boolean().optional(),
  budgetAlerts: z.boolean().optional(),
  subscriptionAlerts: z.boolean().optional(),
  goalAlerts: z.boolean().optional(),
  syncAlerts: z.boolean().optional(),
  securityAlerts: z.boolean().optional(),
  marketingAlerts: z.boolean().optional(),
  dailySummary: z.boolean().optional(),
  weeklySummary: z.boolean().optional(),
  monthlySummary: z.boolean().optional(),
  quietHoursStart: z.string().regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/).optional(),
  quietHoursEnd: z.string().regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/).optional(),
});

// GET /api/v1/notifications
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const page = parseInt(req.query.page as string) || 1;
  const limit = parseInt(req.query.limit as string) || 20;
  const unreadOnly = req.query.unreadOnly === 'true';
  const skip = (page - 1) * limit;

  const where: any = { userId: req.user!.id };
  if (unreadOnly) where.isRead = false;

  const [notifications, total] = await Promise.all([
    prisma.notification.findMany({
      where,
      skip,
      take: limit,
      orderBy: { createdAt: 'desc' },
    }),
    prisma.notification.count({ where }),
  ]);

  res.json({
    success: true,
    data: {
      items: notifications,
      page,
      limit,
      total,
      totalPages: Math.ceil(total / limit),
      hasNext: skip + notifications.length < total,
      hasPrevious: page > 1,
    },
  });
}));

// GET /api/v1/notifications/preferences
router.get('/preferences', asyncHandler(async (req: AuthRequest, res) => {
  const user = await prisma.user.findUnique({
    where: { id: req.user!.id },
    select: {
      notificationsEnabled: true,
      // In production, store notification preferences in a separate table or JSON field
    },
  });

  // Default preferences
  const preferences = {
    transactionAlerts: true,
    budgetAlerts: true,
    subscriptionAlerts: true,
    goalAlerts: true,
    syncAlerts: true,
    securityAlerts: true,
    marketingAlerts: false,
    dailySummary: false,
    weeklySummary: true,
    monthlySummary: true,
    quietHoursStart: '22:00',
    quietHoursEnd: '08:00',
  };

  res.json({ success: true, data: preferences });
}));

// PATCH /api/v1/notifications/preferences
router.patch('/preferences', asyncHandler(async (req: AuthRequest, res) => {
  const data = updatePreferencesSchema.parse(req.body);
  
  // In production, update notification preferences in database
  // For now, just return the updated preferences
  res.json({ success: true, data });
}));

// POST /api/v1/notifications/:id/read
router.post('/:id/read', asyncHandler(async (req: AuthRequest, res) => {
  const notification = await prisma.notification.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!notification) {
    throw new AppError(404, 'Notification not found');
  }

  await prisma.notification.update({
    where: { id: req.params.id },
    data: { isRead: true, readAt: new Date() },
  });

  res.json({ success: true, data: null });
}));

// POST /api/v1/notifications/read-all
router.post('/read-all', asyncHandler(async (req: AuthRequest, res) => {
  await prisma.notification.updateMany({
    where: { userId: req.user!.id, isRead: false },
    data: { isRead: true, readAt: new Date() },
  });

  res.json({ success: true, data: null });
}));

// DELETE /api/v1/notifications/:id
router.delete('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const notification = await prisma.notification.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!notification) {
    throw new AppError(404, 'Notification not found');
  }

  await prisma.notification.delete({ where: { id: req.params.id } });

  res.json({ success: true, data: null });
}));

export { router as notificationRouter };