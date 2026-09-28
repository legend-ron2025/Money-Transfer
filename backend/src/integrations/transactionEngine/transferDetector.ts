import { prisma } from '../../index.js';
import { randomUUID } from 'crypto';

/** Detect and mark internal transfers for a user over the last 90 days.
 *  Returns the number of transactions marked as transfers. */
export async function detectAndMarkTransfers(userId: string): Promise<number> {
  const WINDOW_MS   = 10 * 60 * 1000;   // 10-minute window
  const LOOKBACK_MS = 90 * 24 * 60 * 60 * 1000;
  const since = new Date(Date.now() - LOOKBACK_MS);
  let marked = 0;

  // Fetch all unmatched DEBITs
  const debits = await prisma.transaction.findMany({
    where: { userId, type: 'DEBIT', isTransfer: false, transactionDate: { gte: since } },
    select: { id: true, amount: true, accountId: true, transactionDate: true },
    orderBy: { transactionDate: 'desc' },
  });

  for (const debit of debits) {
    const lo = new Date(debit.transactionDate.getTime() - WINDOW_MS);
    const hi = new Date(debit.transactionDate.getTime() + WINDOW_MS);

    const credit = await prisma.transaction.findFirst({
      where: {
        userId,
        type: 'CREDIT',
        isTransfer: false,
        amount: debit.amount,
        accountId: { not: debit.accountId },
        transactionDate: { gte: lo, lte: hi },
      },
      select: { id: true },
    });

    if (credit) {
      const pairId = randomUUID();
      await prisma.$transaction([
        prisma.transaction.update({ where: { id: debit.id  }, data: { isTransfer: true, transferPairId: pairId } }),
        prisma.transaction.update({ where: { id: credit.id }, data: { isTransfer: true, transferPairId: pairId } }),
      ]);
      marked += 2;
    }
  }
  return marked;
}
