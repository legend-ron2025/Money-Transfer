import { prisma } from '../../index.js';

export interface RecurringPattern {
  merchantName: string;
  averageAmount: number;
  intervalDays: number;
  confidence: number;
  transactionIds: string[];
  nextExpectedDate: Date;
}

const KNOWN_INTERVALS = [7, 14, 28, 30, 90, 365];

/** Detect recurring payments for a user and mark matching transactions.
 *  Returns detected patterns. */
export async function detectRecurringTransactions(userId: string): Promise<RecurringPattern[]> {
  const since = new Date(Date.now() - 90 * 24 * 60 * 60 * 1000);

  const transactions = await prisma.transaction.findMany({
    where: {
      userId,
      type: 'DEBIT',
      isTransfer: false,
      merchantName: { not: null },
      transactionDate: { gte: since },
    },
    orderBy: { transactionDate: 'asc' },
    select: { id: true, merchantName: true, amount: true, transactionDate: true },
  });

  // Group by merchant name
  const byMerchant: Record<string, typeof transactions> = {};
  for (const t of transactions) {
    const key = t.merchantName!;
    (byMerchant[key] ??= []).push(t);
  }

  const patterns: RecurringPattern[] = [];

  for (const [merchant, txns] of Object.entries(byMerchant)) {
    if (txns.length < 2) continue;

    // All amounts must be within 5 % of each other
    const amounts = txns.map(t => Number(t.amount));
    const avg     = amounts.reduce((a, b) => a + b, 0) / amounts.length;
    if (amounts.some(a => Math.abs(a - avg) > avg * 0.05)) continue;

    // Calculate day-intervals between consecutive transactions
    const dates = txns.map(t => t.transactionDate.getTime()).sort((a, b) => a - b);
    const intervals: number[] = [];
    for (let i = 1; i < dates.length; i++) {
      intervals.push(Math.round((dates[i] - dates[i - 1]) / 86_400_000));
    }

    const avgInterval = intervals.reduce((a, b) => a + b, 0) / intervals.length;
    if (intervals.some(iv => Math.abs(iv - avgInterval) > 3)) continue;

    // Must match a known billing interval
    const matched = KNOWN_INTERVALS.find(ki => Math.abs(avgInterval - ki) <= 3);
    if (!matched) continue;

    // Mark all as recurring
    await prisma.transaction.updateMany({
      where: { id: { in: txns.map(t => t.id) } },
      data:  { isRecurring: true },
    });

    const lastDate = new Date(Math.max(...dates));
    patterns.push({
      merchantName:     merchant,
      averageAmount:    avg,
      intervalDays:     matched,
      confidence:       txns.length >= 3 ? 0.9 : 0.75,
      transactionIds:   txns.map(t => t.id),
      nextExpectedDate: new Date(lastDate.getTime() + matched * 86_400_000),
    });
  }

  return patterns;
}
