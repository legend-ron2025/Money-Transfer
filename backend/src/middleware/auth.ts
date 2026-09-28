import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { prisma } from '../index';

const JWT_SECRET = process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-in-production';

export interface AuthRequest extends Request {
  user?: {
    id: string;
    email: string;
    deviceId?: string;
  };
}

export const authMiddleware = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      res.status(401).json({ error: 'No token provided' });
      return;
    }

    const token = authHeader.split(' ')[1];
    
    const decoded = jwt.verify(token, JWT_SECRET) as {
      id: string;
      email: string;
      deviceId?: string;
    };

    // Verify user exists and is active
    const user = await prisma.user.findUnique({
      where: { id: decoded.id },
      select: { id: true, email: true, isEmailVerified: true },
    });

    if (!user) {
      res.status(401).json({ error: 'User not found' });
      return;
    }

    req.user = decoded;
    next();
  } catch (error) {
    if (error instanceof jwt.TokenExpiredError) {
      res.status(401).json({ error: 'Token expired' });
      return;
    }
    if (error instanceof jwt.JsonWebTokenError) {
      res.status(401).json({ error: 'Invalid token' });
      return;
    }
    res.status(500).json({ error: 'Authentication failed' });
  }
};

export const generateTokens = (userId: string, email: string, deviceId?: string) => {
  const accessToken = jwt.sign(
    { id: userId, email, deviceId },
    JWT_SECRET,
    { expiresIn: '15m' }
  );

  const refreshToken = jwt.sign(
    { id: userId, email, deviceId, type: 'refresh' },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  return { accessToken, refreshToken };
};

export const verifyRefreshToken = (token: string) => {
  return jwt.verify(token, JWT_SECRET) as {
    id: string;
    email: string;
    deviceId?: string;
    type: string;
  };
};