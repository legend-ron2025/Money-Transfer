import { Router } from 'express';
import bcrypt from 'bcryptjs';
import { z } from 'zod';
import { prisma } from '../index';
import { generateTokens, verifyRefreshToken, AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
  deviceId: z.string().optional(),
  deviceName: z.string().optional(),
  platform: z.string().optional(),
  fcmToken: z.string().optional(),
});

const registerSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
  name: z.string().min(2).max(100).optional(),
  deviceId: z.string().optional(),
  deviceName: z.string().optional(),
  platform: z.string().optional(),
  fcmToken: z.string().optional(),
});

const refreshSchema = z.object({
  refreshToken: z.string(),
});

// POST /api/v1/auth/login
router.post('/login', asyncHandler(async (req, res) => {
  const data = loginSchema.parse(req.body);
  
  const user = await prisma.user.findUnique({
    where: { email: data.email },
  });

  if (!user) {
    throw new AppError(401, 'Invalid credentials');
  }

  const isValidPassword = await bcrypt.compare(data.password, user.passwordHash);
  if (!isValidPassword) {
    throw new AppError(401, 'Invalid credentials');
  }

  // Update device info
  if (data.deviceId) {
    await prisma.userDevice.upsert({
      where: { deviceId: data.deviceId },
      update: {
        platform: data.platform,
        lastSeen: new Date(),
        isTrusted: true,
      },
      create: {
        userId: user.id,
        deviceId: data.deviceId,
        platform: data.platform || 'android',
        isTrusted: true,
      },
    });
  }

  const tokens = generateTokens(user.id, user.email, data.deviceId);

  res.json({
    success: true,
    data: {
      user: {
        id: user.id,
        email: user.email,
        phone: user.phone,
        name: user.name,
        avatar: user.avatar,
        isEmailVerified: user.isEmailVerified,
        isPhoneVerified: user.isPhoneVerified,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
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
      accessToken: tokens.accessToken,
      refreshToken: tokens.refreshToken,
      expiresIn: 900, // 15 minutes
      tokenType: 'Bearer',
    },
  });
}));

// POST /api/v1/auth/register
router.post('/register', asyncHandler(async (req, res) => {
  const data = registerSchema.parse(req.body);

  const existingUser = await prisma.user.findUnique({
    where: { email: data.email },
  });

  if (existingUser) {
    throw new AppError(409, 'Email already registered');
  }

  const passwordHash = await bcrypt.hash(data.password, 12);

  const user = await prisma.user.create({
    data: {
      email: data.email,
      passwordHash,
      name: data.name,
      currency: 'INR',
      language: 'en',
      theme: 'system',
    },
  });

  // Create default categories for user
  await createDefaultCategories(user.id);

  // Update device info
  if (data.deviceId) {
    await prisma.userDevice.create({
      data: {
        userId: user.id,
        deviceId: data.deviceId,
        platform: data.platform || 'android',
        isTrusted: true,
      },
    });
  }

  const tokens = generateTokens(user.id, user.email, data.deviceId);

  res.status(201).json({
    success: true,
    data: {
      user: {
        id: user.id,
        email: user.email,
        phone: user.phone,
        name: user.name,
        avatar: user.avatar,
        isEmailVerified: user.isEmailVerified,
        isPhoneVerified: user.isPhoneVerified,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
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
      accessToken: tokens.accessToken,
      refreshToken: tokens.refreshToken,
      expiresIn: 900,
      tokenType: 'Bearer',
    },
  });
}));

// POST /api/v1/auth/refresh
router.post('/refresh', asyncHandler(async (req, res) => {
  const data = refreshSchema.parse(req.body);

  try {
    const decoded = verifyRefreshToken(data.refreshToken);
    
    if (decoded.type !== 'refresh') {
      throw new AppError(401, 'Invalid token type');
    }

    const user = await prisma.user.findUnique({
      where: { id: decoded.id },
      select: { id: true, email: true },
    });

    if (!user) {
      throw new AppError(401, 'User not found');
    }

    const tokens = generateTokens(user.id, user.email, decoded.deviceId);

    res.json({
      success: true,
      data: {
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
        expiresIn: 900,
        tokenType: 'Bearer',
      },
    });
  } catch (error) {
    throw new AppError(401, 'Invalid refresh token');
  }
}));

// POST /api/v1/auth/logout
router.post('/logout', asyncHandler(async (req: AuthRequest, res) => {
  // In a production app, you would blacklist the token here
  res.json({ success: true, data: null });
}));

// POST /api/v1/auth/forgot-password
router.post('/forgot-password', asyncHandler(async (req, res) => {
  const { email } = z.object({ email: z.string().email() }).parse(req.body);
  
  const user = await prisma.user.findUnique({ where: { email } });
  
  // Always return success to prevent email enumeration
  // In production, send reset email here
  res.json({ success: true, data: null });
}));

// POST /api/v1/auth/reset-password
router.post('/reset-password', asyncHandler(async (req, res) => {
  const { token, newPassword, confirmPassword } = z.object({
    token: z.string(),
    newPassword: z.string().min(8),
    confirmPassword: z.string(),
  }).parse(req.body);

  if (newPassword !== confirmPassword) {
    throw new AppError(400, 'Passwords do not match');
  }

  // In production, verify reset token and update password
  // For now, just return success
  res.json({ success: true, data: null });
}));

async function createDefaultCategories(userId: string) {
  const categories = [
    // Income categories
    { name: 'Salary', parentId: null, icon: 'briefcase', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 1 },
    { name: 'Freelance', parentId: null, icon: 'laptop', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 2 },
    { name: 'Business', parentId: null, icon: 'store', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 3 },
    { name: 'Interest', parentId: null, icon: 'percent', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 4 },
    { name: 'Investment', parentId: null, icon: 'trending_up', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 5 },
    { name: 'Cashback', parentId: null, icon: 'card_giftcard', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 6 },
    { name: 'Other Income', parentId: null, icon: 'add_circle', type: 'INCOME', color: '#4CAF50', isSystem: true, sortOrder: 99 },
    
    // Expense categories - parent categories
    { name: 'Food', parentId: null, icon: 'restaurant', type: 'EXPENSE', color: '#FF9800', isSystem: true, sortOrder: 1 },
    { name: 'Transport', parentId: null, icon: 'directions_car', type: 'EXPENSE', color: '#2196F3', isSystem: true, sortOrder: 2 },
    { name: 'Shopping', parentId: null, icon: 'shopping_bag', type: 'EXPENSE', color: '#9C27B0', isSystem: true, sortOrder: 3 },
    { name: 'Bills', parentId: null, icon: 'receipt_long', type: 'EXPENSE', color: '#F44336', isSystem: true, sortOrder: 4 },
    { name: 'Entertainment', parentId: null, icon: 'movie', type: 'EXPENSE', color: '#E91E63', isSystem: true, sortOrder: 5 },
    { name: 'Health', parentId: null, icon: 'local_hospital', type: 'EXPENSE', color: '#00BCD4', isSystem: true, sortOrder: 6 },
    { name: 'Education', parentId: null, icon: 'school', type: 'EXPENSE', color: '#795548', isSystem: true, sortOrder: 7 },
    { name: 'Travel', parentId: null, icon: 'flight', type: 'EXPENSE', color: '#607D8B', isSystem: true, sortOrder: 8 },
    { name: 'Insurance', parentId: null, icon: 'security', type: 'EXPENSE', color: '#3F51B5', isSystem: true, sortOrder: 9 },
    { name: 'Investments', parentId: null, icon: 'trending_up', type: 'EXPENSE', color: '#673AB7', isSystem: true, sortOrder: 10 },
    { name: 'EMI/Loans', parentId: null, icon: 'account_balance', type: 'EXPENSE', color: '#FF5722', isSystem: true, sortOrder: 11 },
    { name: 'Other', parentId: null, icon: 'more_horiz', type: 'EXPENSE', color: '#9E9E9E', isSystem: true, sortOrder: 99 },
  ];

  // Create parent categories first
  const createdCategories = await Promise.all(
    categories.map(cat => prisma.category.create({
      data: { ...cat, userId: null } // System categories have null userId
    }))
  );

  // Create child categories
  const foodParent = createdCategories.find(c => c.name === 'Food')!;
  const transportParent = createdCategories.find(c => c.name === 'Transport')!;
  const shoppingParent = createdCategories.find(c => c.name === 'Shopping')!;
  const billsParent = createdCategories.find(c => c.name === 'Bills')!;
  const entertainmentParent = createdCategories.find(c => c.name === 'Entertainment')!;

  const childCategories = [
    // Food children
    { name: 'Restaurants', parentId: foodParent.id, icon: 'restaurant', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 1 },
    { name: 'Food Delivery', parentId: foodParent.id, icon: 'delivery_dining', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 2 },
    { name: 'Groceries', parentId: foodParent.id, icon: 'shopping_cart', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 3 },
    { name: 'Snacks', parentId: foodParent.id, icon: 'cookie', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 4 },
    
    // Transport children
    { name: 'Fuel', parentId: transportParent.id, icon: 'local_gas_station', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 1 },
    { name: 'Cab/Ride Share', parentId: transportParent.id, icon: 'local_taxi', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 2 },
    { name: 'Public Transport', parentId: transportParent.id, icon: 'directions_bus', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 3 },
    { name: 'Parking', parentId: transportParent.id, icon: 'local_parking', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 4 },
    
    // Shopping children
    { name: 'Clothing', parentId: shoppingParent.id, icon: 'checkroom', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 1 },
    { name: 'Electronics', parentId: shoppingParent.id, icon: 'devices', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 2 },
    { name: 'Other', parentId: shoppingParent.id, icon: 'category', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 99 },
    
    // Bills children
    { name: 'Electricity', parentId: billsParent.id, icon: 'bolt', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 1 },
    { name: 'Mobile', parentId: billsParent.id, icon: 'phone_android', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 2 },
    { name: 'Internet', parentId: billsParent.id, icon: 'wifi', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 3 },
    { name: 'Water', parentId: billsParent.id, icon: 'water_drop', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 4 },
    
    // Entertainment children
    { name: 'Streaming', parentId: entertainmentParent.id, icon: 'tv', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 1 },
    { name: 'Gaming', parentId: entertainmentParent.id, icon: 'sports_esports', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 2 },
    { name: 'Movies', parentId: entertainmentParent.id, icon: 'movie', type: 'EXPENSE', color: null, isSystem: true, sortOrder: 3 },
  ];

  await Promise.all(
    childCategories.map(cat => prisma.category.create({
      data: { ...cat, userId: null }
    }))
  );
}

export { router as authRouter };