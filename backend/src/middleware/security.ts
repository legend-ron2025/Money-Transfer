/**
 * Security middleware: IDOR protection, audit logging, input sanitisation.
 */
import { Request, Response, NextFunction } from 'express';
import { prisma } from '../index.js';
import { AuthRequest } from './auth.js';

// ── IDOR guard ────────────────────────────────────────────────────────────────
/** Confirms that req.params.userId (if present) matches the authenticated user.
 *  Also used to guard resource-level access in route handlers. */
export function ownUserOnly(req: AuthRequest, res: Response, next: NextFunction): void {
  const paramId = req.params.userId;
  if (paramId && paramId !== req.user?.id) {
    res.status(403).json({ error: 'Forbidden — you cannot access another user\'s data' });
    return;
  }
  next();
}

// ── Audit logger ──────────────────────────────────────────────────────────────
export async function auditLog(
  userId: string | undefined,
  action:  string,
  opts?: { entityType?: string; entityId?: string; metadata?: Record<string, unknown>; ipAddress?: string },
): Promise<void> {
  try {
    await prisma.auditLog.create({
      data: {
        userId:     userId ?? null,
        action,
        entityType: opts?.entityType ?? null,
        entityId:   opts?.entityId   ?? null,
        ipAddress:  opts?.ipAddress  ?? null,
        metadata:   opts?.metadata ? JSON.parse(JSON.stringify(opts.metadata)) : undefined,
      },
    });
  } catch (err) {
    // Audit failures must never crash the request
    console.error('[AuditLog] Failed to write:', err);
  }
}

/** Express middleware that logs every authenticated mutation (POST/PATCH/DELETE). */
export function auditMiddleware(req: AuthRequest, _res: Response, next: NextFunction): void {
  if (['POST', 'PATCH', 'DELETE', 'PUT'].includes(req.method) && req.user) {
    const ip = (req.headers['x-forwarded-for'] as string)?.split(',')[0].trim() ?? req.socket?.remoteAddress;
    auditLog(req.user.id, `${req.method} ${req.path}`, {
      ipAddress: ip,
      metadata:  { userAgent: req.headers['user-agent'] },
    }).catch(() => {});
  }
  next();
}

// ── Input sanitisation ────────────────────────────────────────────────────────
/** Strips dangerous HTML / script characters from all string fields in req.body. */
export function sanitiseBody(req: Request, _res: Response, next: NextFunction): void {
  if (req.body && typeof req.body === 'object') {
    req.body = deepSanitise(req.body);
  }
  next();
}

function deepSanitise(obj: unknown): unknown {
  if (typeof obj === 'string') {
    return obj
      .replace(/<script[^>]*>.*?<\/script>/gis, '')
      .replace(/<[^>]+>/g, '')        // strip HTML tags
      .replace(/javascript:/gi, '')   // strip JS URIs
      .trim();
  }
  if (Array.isArray(obj)) return obj.map(deepSanitise);
  if (obj && typeof obj === 'object') {
    return Object.fromEntries(
      Object.entries(obj as Record<string, unknown>).map(([k, v]) => [k, deepSanitise(v)])
    );
  }
  return obj;
}

// ── Security headers ──────────────────────────────────────────────────────────
export function securityHeaders(_req: Request, res: Response, next: NextFunction): void {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options',        'DENY');
  res.setHeader('Referrer-Policy',        'strict-origin-when-cross-origin');
  res.setHeader('Permissions-Policy',     'geolocation=(), camera=(), microphone=()');
  next();
}
