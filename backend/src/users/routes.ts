import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const updateProfileSchema = z.object({
  name: z.string().min(2).max(100).optional(),
  phone: z.string().optional(),
  avatar: z.string().url().optional(),
});

const updatePreferencesSchema = z.object({
  currency: z.string().length(3).optional(),
  language: z.string().length(2).optional(),
  theme: z.enum(['light', 'dark', 'system']).optional(),
  notificationsEnabled: z.boolean().optional(),
  biometricEnabled: z.boolean().optional(),
  autoSyncEnabled: z.boolean().optional(),
  syncFrequency: z.enum(['realtime', '5min', '15min', '30min', 'hourly', 'daily']).optional(),
});

// GET /api/v1/users/me
router.get('/me', asyncHandler(async (req: AuthRequest, res) => {
  const user = await prisma.user.findUnique({
    where: { id: req.user!.id },
    select: {
      id: true,
      email: true,
      phone: true,
      name: true,
      avatar: true,
      isEmailVerified: true,
      isPhoneVerified: true,
      currency: true,
      language: true,
      theme: true,
      notificationsEnabled: true,
      biometricEnabled: true,
      autoSyncEnabled: true,
      syncFrequency: true,
      createdAt: true,
      updatedAt: true,
    },
  });

  if (!user) {
    throw new AppError(404, 'User not found');
  }

  res.json({
    success: true,
    data: {
      ...user,
      preferences: {
        currency: user.currency,
        language: user.language,
        theme: user.theme,
        notificationsEnabled: user.notificationsEnabled,
        biometricEnabled: user.biometricEnabled,
        autoSyncEnabled: user.autoSyncEnabled,
        syncFrequency: user.syncFrequency,
      },
    },
  });
}));

// PATCH /api/v1/users/me
router.patch('/me', asyncHandler(async (req: AuthRequest, res) => {
  const data = updateProfileSchema.parse(req.body);

  const user = await prisma.user.update({
    where: { id: req.user!.id },
    data: {
      name: data.name,
      phone: data.phone,
      avatar: data.avatar,
    },
    select: {
      id: true,
      email: true,
      phone: true,
      name: true,
      avatar: true,
      isEmailVerified: true,
      isPhoneVerified: true,
      currency: true,
      language: true,
      theme: true,
      notificationsEnabled: true,
      biometricEnabled: true,
      autoSyncEnabled: true,
      syncFrequency: true,
      createdAt: true,
      updatedAt: true,
    },
  });

  res.json({
    success: true,
    data: {
      ...user,
      preferences: {
        currency: user.currency,
        language: user.language,
        theme: user.theme,
        notificationsEnabled: user.notificationsEnabled,
        biometricEnabled: user.biometricEnabled,
        autoSyncEnabled: user.autoSyncEnabled,
        syncFrequency: user.syncFrequency,
      },
    },
  });
}));

// PATCH /api/v1/users/me/preferences
router.patch('/me/preferences', asyncHandler(async (req: AuthRequest, res) => {
  const data = updatePreferencesSchema.parse(req.body);

  const user = await prisma.user.update({
    where: { id: req.user!.id },
    data: {
      currency: data.currency,
      language: data.language,
      theme: data.theme,
      notificationsEnabled: data.notificationsEnabled,
      biometricEnabled: data.biometricEnabled,
      autoSyncEnabled: data.autoSyncEnabled,
      syncFrequency: data.syncFrequency,
    },
    select: {
      id: true,
      email: true,
      phone: true,
      name: true,
      avatar: true,
      isEmailVerified: true,
      isPhoneVerified: true,
      currency: true,
      language: true,
      theme: true,
      notificationsEnabled: true,
      biometricEnabled: true,
      autoSyncEnabled: true,
      syncFrequency: true,
      createdAt: true,
      updatedAt: true,
    },
  });

  res.json({
    success: true,
    data: {
      ...user,
      preferences: {
        currency: user.currency,
        language: user.language,
        theme: user.theme,
        notificationsEnabled: user.notificationsEnabled,
        biometricEnabled: user.biometricEnabled,
        autoSyncEnabled: user.autoSyncEnabled,
        syncFrequency: user.syncFrequency,
      },
    },
  });
}));

export { router as userRouter };