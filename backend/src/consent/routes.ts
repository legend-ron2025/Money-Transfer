import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index';
import { AuthRequest } from '../middleware/auth';
import { asyncHandler, AppError } from '../middleware/errorHandler';

const router = Router();

const createConsentSchema = z.object({
  providerId: z.string().uuid(),
  accounts: z.array(z.string().uuid()),
  permissions: z.array(z.string()),
  redirectUrl: z.string().url(),
});

// GET /api/v1/consents
router.get('/', asyncHandler(async (req: AuthRequest, res) => {
  const consents = await prisma.consent.findMany({
    where: { userId: req.user!.id },
    orderBy: { grantedAt: 'desc' },
  });

  res.json({
    success: true,
    data: consents.map(c => ({
      ...c,
      accounts: c.accounts as any[],
    })),
  });
}));

// GET /api/v1/consents/:id
router.get('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const consent = await prisma.consent.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!consent) {
    throw new AppError(404, 'Consent not found');
  }

  res.json({
    success: true,
    data: { ...consent, accounts: consent.accounts as any[] },
  });
}));

// POST /api/v1/consents
router.post('/', asyncHandler(async (req: AuthRequest, res) => {
  const data = createConsentSchema.parse(req.body);

  // In production, initiate AA consent flow here
  // For now, create consent record with pending status
  const consent = await prisma.consent.create({
    data: {
      userId: req.user!.id,
      providerId: data.providerId,
      providerName: 'Sample Provider',
      providerLogo: 'https://example.com/logo.png',
      consentId: `consent_${Date.now()}`,
      status: 'PENDING',
      grantedAt: new Date(),
      accounts: data.accounts,
      permissions: data.permissions,
    },
  });

  // Return consent URL for user to approve
  res.status(201).json({
    success: true,
    data: {
      ...consent,
      accounts: consent.accounts as any[],
      consentUrl: `https://aa.example.com/consent/${consent.consentId}?redirect=${encodeURIComponent(data.redirectUrl)}`,
    },
  });
}));

// DELETE /api/v1/consents/:id
router.delete('/:id', asyncHandler(async (req: AuthRequest, res) => {
  const consent = await prisma.consent.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!consent) {
    throw new AppError(404, 'Consent not found');
  }

  // In production, call AA revoke API
  await prisma.consent.update({
    where: { id: req.params.id },
    data: { status: 'REVOKED', revokedAt: new Date() },
  });

  res.json({ success: true, data: null });
}));

// POST /api/v1/consents/:id/refresh
router.post('/:id/refresh', asyncHandler(async (req: AuthRequest, res) => {
  const consent = await prisma.consent.findFirst({
    where: { id: req.params.id, userId: req.user!.id },
  });

  if (!consent) {
    throw new AppError(404, 'Consent not found');
  }

  // In production, call AA refresh API
  const updated = await prisma.consent.update({
    where: { id: req.params.id },
    data: { status: 'ACTIVE', updatedAt: new Date() },
  });

  res.json({ success: true, data: { ...updated, accounts: updated.accounts as any[] } });
}));

// GET /api/v1/consents/providers
router.get('/providers', asyncHandler(async (req: AuthRequest, res) => {
  // Return list of available AA providers
  const providers = [
    {
      id: 'provider_1',
      name: 'Sample AA Provider',
      logo: 'https://example.com/logo.png',
      type: 'AA',
      country: 'IN',
      supportedAccountTypes: ['SAVINGS', 'CURRENT', 'CREDIT'],
      supportedPermissions: ['TRANSACTIONS', 'BALANCE', 'IDENTITY'],
      isActive: true,
    },
  ];

  res.json({ success: true, data: providers });
}));

export { router as consentRouter };