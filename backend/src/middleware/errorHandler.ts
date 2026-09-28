import { Request, Response, NextFunction } from 'express';
import { ZodError } from 'zod';
import { Prisma } from '@prisma/client';

export class AppError extends Error {
  constructor(
    public statusCode: number,
    public message: string,
    public code?: string,
    public details?: any
  ) {
    super(message);
    Object.setPrototypeOf(this, AppError.prototype);
  }
}

export const errorHandler = (
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
): void => {
  console.error('Error:', err);

  if (err instanceof ZodError) {
    res.status(400).json({
      error: 'Validation error',
      details: err.errors.map(e => ({
        field: e.path.join('.'),
        message: e.message,
      })),
    });
    return;
  }

  if (err instanceof Prisma.PrismaClientKnownRequestError) {
    if (err.code === 'P2002') {
      const field = (err.meta?.target as string[])?.join(', ') || 'field';
      res.status(409).json({
        error: 'Conflict',
        message: `${field} already exists`,
      });
      return;
    }
    if (err.code === 'P2025') {
      res.status(404).json({
        error: 'Not found',
        message: 'Record not found',
      });
      return;
    }
  }

  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      error: err.code || 'Error',
      message: err.message,
      details: err.details,
    });
    return;
  }

  // Default error
  res.status(500).json({
    error: 'Internal server error',
    message: process.env.NODE_ENV === 'development' ? err.message : 'Something went wrong',
  });
};

export const asyncHandler = (fn: Function) => {
  return (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
};