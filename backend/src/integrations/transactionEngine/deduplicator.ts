import { prisma } from '../../index.js';

export interface IncomingForDedup {
  externalId?: string | null;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  transactionDate: Date;
  upiReference?: string | null;
  accountId: string;
  userId: string;
}

/** Returns true if a matching transaction already exists. */
export async function isDuplicate(t: IncomingForDedup): Promise<boolean> {
  // 1. Exact external ID + account
  if (t.externalId) {
    const found = await prisma.transaction.findFirst({
      where: { externalId: t.externalId, accountId: t.accountId },
      select: { id: true },
    });
    if (found) return true;
  }

  // 2. UPI reference (unique across all accounts for a user)
  if (t.upiReference) {
    const found = await prisma.transaction.findFirst({
      where: { upiReference: t.upiReference, userId: t.userId },
      select: { id: true },
    });
    if (found) return true;
  }

  // 3. Fuzzy: same amount + type + account within ±5 minutes
  const window = 5 * 60 * 1000;
  const found = await prisma.transaction.findFirst({
    where: {
      userId:    t.userId,
      accountId: t.accountId,
      amount:    t.amount,
      type:      t.type,
      transactionDate: {
        gte: new Date(t.transactionDate.getTime() - window),
        lte: new Date(t.transactionDate.getTime() + window),
      },
    },
    select: { id: true },
  });
  return !!found;
}

/** Filter an array, removing entries already stored. */
export async function filterDuplicates<T extends IncomingForDedup>(items: T[]): Promise<T[]> {
  const out: T[] = [];
  for (const item of items) {
    if (!(await isDuplicate(item))) out.push(item);
  }
  return out;
}
