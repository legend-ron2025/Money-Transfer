/**
 * POST /api/v1/consents/webhook
 * Setu calls this endpoint when consent status changes.
 * No auth middleware — Setu calls it directly.
 * Verified by checking x-setu-signature header (HMAC-SHA256).
 */
import { Router } from 'express';
import crypto from 'crypto';
import { prisma } from '../../index.js';
import { aaDataSyncQueue } from '../../queues/index.js';

const router = Router();
const WEBHOOK_SECRET = process.env.SETU_WEBHOOK_SECRET ?? '';

function verifySignature(body: string, signature: string): boolean {
  if (!WEBHOOK_SECRET) return true; // Skip in dev when secret not configured
  const expected = crypto
    .createHmac('sha256', WEBHOOK_SECRET)
    .update(body)
    .digest('hex');
  return crypto.timingSafeEqual(Buffer.from(signature), Buffer.from(expected));
}

router.post('/', async (req, res) => {
  const raw       = JSON.stringify(req.body);
  const signature = (req.headers['x-setu-signature'] ?? '') as string;

  if (!verifySignature(raw, signature)) {
    console.warn('[AAWebhook] Invalid signature — rejected');
    res.status(401).json({ error: 'Invalid signature' });
    return;
  }

  const { consentHandle, consentId, status } = req.body as {
    consentHandle?: string;
    consentId?:     string;
    status?:        string;
  };

  const handle = consentHandle ?? consentId;
  if (!handle) { res.status(400).json({ error: 'Missing consentHandle' }); return; }

  console.log(`[AAWebhook] Consent ${handle} → ${status}`);

  // Update consent record
  const consent = await prisma.consent.findFirst({ where: { consentId: handle } });
  if (consent) {
    await prisma.consent.update({
      where: { id: consent.id },
      data: {
        status:    status ?? consent.status,
        updatedAt: new Date(),
        ...(status === 'REVOKED' ? { revokedAt: new Date() } : {}),
      },
    });

    // On ACTIVE — trigger AA data fetch
    if (status === 'ACTIVE' || status === 'GRANTED') {
      const accounts = Array.isArray(consent.accounts) ? (consent.accounts as any[]) : [];
      await aaDataSyncQueue.add('fetch', {
        userId:     consent.userId,
        consentId:  handle,
        accountIds: accounts.map((a: any) => a.id ?? a).filter(Boolean),
        dateRange:  {
          from: new Date(Date.now() - 180 * 24 * 60 * 60 * 1000).toISOString(),
          to:   new Date().toISOString(),
        },
      });
    }
  }

  res.json({ success: true });
});

export { router as consentWebhookRouter };
