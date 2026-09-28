import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../index.js';
import { AuthRequest } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';

const router = Router();

const periodSchema = z.object({
  period: z.enum(['today', 'week', 'month', 'quarter', 'year']).default('month'),
});

function getDateRange(period: string): { start: Date; end: Date } {
  const now  = new Date();
  const end  = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 999);
  let   start: Date;
  switch (period) {
    case 'today':   start = new Date(now.getFullYear(), now.getMonth(), now.getDate()); break;
    case 'week':    start = new Date(now); start.setDate(now.getDate() - now.getDay()); start.setHours(0,0,0,0); break;
    case 'month':   start = new Date(now.getFullYear(), now.getMonth(), 1); break;
    case 'quarter': start = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3, 1); break;
    case 'year':    start = new Date(now.getFullYear(), 0, 1); break;
    default:        start = new Date(now.getFullYear(), now.getMonth(), 1);
  }
  return { start, end };
}

// Helper to read / write 1-hour analytics cache
async function withCache<T>(userId: string, key: string, period: string, compute: () => Promise<T>): Promise<T> {
  const cached = await prisma.analyticsCache.findFirst({
    where: { userId, cacheKey: key },
  });
  if (cached && new Date(cached.expiresAt) > new Date()) {
    return cached.data as unknown as T;
  }
  const data     = await compute();
  const expiresAt = new Date(Date.now() + 60 * 60 * 1000);
  await prisma.analyticsCache.upsert({
    where:  { userId_cacheKey: { userId, cacheKey: key } },
    update: { data: data as any, expiresAt },
    create: { userId, cacheKey: key, data: data as any, period, expiresAt },
  });
  return data;
}

// ── GET /api/v1/analytics/overview ──────────────────────────────────────────
router.get('/overview', asyncHandler(async (req: AuthRequest, res) => {
  const { period } = periodSchema.parse(req.query);
  const userId     = req.user!.id;
  const { start, end } = getDateRange(period);

  const data = await withCache(userId, `overview_${period}`, period, async () => {
    // Account balances
    const accounts = await prisma.account.findMany({
      where:   { userId, isActive: true },
      include: { institution: { select: { name: true, logo: true, type: true } } },
    });
    const totalBalance = accounts.reduce((s, a) => s + Number(a.balance), 0);

    // Current period P&L
    const txns = await prisma.transaction.findMany({
      where: { userId, transactionDate: { gte: start, lte: end }, isTransfer: false },
      include: { category: true },
    });
    const income  = txns.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
    const expense = txns.filter(t => t.type === 'DEBIT' ).reduce((s, t) => s + Number(t.amount), 0);

    // Previous period comparison
    const periodMs  = end.getTime() - start.getTime();
    const prevStart = new Date(start.getTime() - periodMs);
    const prevEnd   = new Date(start.getTime() - 1);
    const prevTxns  = await prisma.transaction.findMany({
      where: { userId, transactionDate: { gte: prevStart, lte: prevEnd }, isTransfer: false },
    });
    const prevIncome  = prevTxns.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
    const prevExpense = prevTxns.filter(t => t.type === 'DEBIT' ).reduce((s, t) => s + Number(t.amount), 0);

    // Category breakdown
    const catMap: Record<string, { categoryId: string; categoryName: string; icon: string; amount: number; count: number }> = {};
    for (const t of txns.filter(t => t.type === 'DEBIT' && t.category)) {
      const c = t.category!;
      if (!catMap[c.id]) catMap[c.id] = { categoryId: c.id, categoryName: c.name, icon: c.icon, amount: 0, count: 0 };
      catMap[c.id].amount += Number(t.amount);
      catMap[c.id].count++;
    }
    const topCategories = Object.values(catMap)
      .sort((a, b) => b.amount - a.amount)
      .slice(0, 5)
      .map(c => ({ ...c, percentage: expense > 0 ? (c.amount / expense) * 100 : 0, transactionCount: c.count }));

    // Recent transactions (10)
    const recentTransactions = await prisma.transaction.findMany({
      where:   { userId },
      orderBy: { transactionDate: 'desc' },
      take:    10,
      include: {
        account:  { include: { institution: { select: { name: true, logo: true } } } },
        category: true,
      },
    });

    // Upcoming payments from recurring transactions
    const recurring = await prisma.transaction.findMany({
      where: { userId, isRecurring: true, type: 'DEBIT' },
      orderBy: { transactionDate: 'desc' },
      take: 50,
    });
    // Group by merchant, keep most recent per merchant
    const merchantLastSeen: Record<string, { merchantName: string; amount: number; lastDate: Date }> = {};
    for (const t of recurring) {
      const m = t.merchantName ?? t.description;
      if (!merchantLastSeen[m] || t.transactionDate > merchantLastSeen[m].lastDate) {
        merchantLastSeen[m] = { merchantName: m, amount: Number(t.amount), lastDate: t.transactionDate };
      }
    }
    const upcomingPayments = Object.values(merchantLastSeen)
      .map(r => ({
        id:           r.merchantName,
        name:         r.merchantName,
        amount:       r.amount,
        dueDate:      new Date(r.lastDate.getTime() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        type:         'SUBSCRIPTION',
        merchantName: r.merchantName,
      }))
      .slice(0, 5);

    // Budget alerts
    const activeBudgets = await prisma.budget.findMany({
      where:   { userId, isActive: true },
      include: { category: true },
    });
    const budgetAlerts = activeBudgets
      .map(b => {
        const pct = Number(b.amount) > 0 ? (Number(b.spentAmount) / Number(b.amount)) * 100 : 0;
        let status = 'NORMAL';
        if (pct >= 100) status = 'EXCEEDED';
        else if (pct >= 90) status = 'NEAR_LIMIT';
        else if (pct >= 70) status = 'WATCH';
        return { budgetId: b.id, categoryName: b.category.name, percentageUsed: pct, status };
      })
      .filter(b => b.status !== 'NORMAL');

    return {
      totalBalance,
      totalIncome:  income,
      totalExpense: expense,
      netSavings:   income - expense,
      savingsRate:  income > 0 ? ((income - expense) / income) * 100 : 0,
      balanceChange: totalBalance,
      incomeChange:  income - prevIncome,
      expenseChange: expense - prevExpense,
      period,
      currency: 'INR',
      accountBalances: accounts.map(a => ({
        accountId:   a.id,
        accountName: `${a.institution.name} ${a.accountType}`,
        balance:     Number(a.balance),
        currency:    a.currency,
        accountType: a.accountType,
      })),
      topCategories,
      recentTransactions: recentTransactions.map(t => ({
        ...t,
        amount:           Number(t.amount),
        account: t.account ? {
          id:              t.account.id,
          maskedNumber:    t.account.maskedNumber,
          institutionName: t.account.institution.name,
          accountType:     t.account.accountType,
        } : null,
      })),
      upcomingPayments,
      budgetAlerts,
    };
  });

  res.json({ success: true, data });
}));

// ── GET /api/v1/analytics/spending ──────────────────────────────────────────
router.get('/spending', asyncHandler(async (req: AuthRequest, res) => {
  const { period } = periodSchema.parse(req.query);
  const userId     = req.user!.id;
  const { start, end } = getDateRange(period);

  const data = await withCache(userId, `spending_${period}`, period, async () => {
    const txns = await prisma.transaction.findMany({
      where:   { userId, transactionDate: { gte: start, lte: end }, isTransfer: false, type: 'DEBIT' },
      include: { category: true },
    });

    const total        = txns.reduce((s, t) => s + Number(t.amount), 0);
    const days         = Math.max(1, Math.ceil((end.getTime() - start.getTime()) / 86_400_000));
    const averageDaily = total / days;

    // By category
    const catMap: Record<string, any> = {};
    for (const t of txns) {
      const key = t.category?.id ?? 'uncategorized';
      if (!catMap[key]) catMap[key] = { categoryId: key, categoryName: t.category?.name ?? 'Uncategorized', icon: t.category?.icon ?? '💰', amount: 0, count: 0 };
      catMap[key].amount += Number(t.amount);
      catMap[key].count++;
    }
    const byCategory = Object.values(catMap)
      .sort((a: any, b: any) => b.amount - a.amount)
      .map((c: any) => ({ ...c, percentage: total > 0 ? (c.amount / total) * 100 : 0, transactionCount: c.count }));

    // By merchant
    const merchantMap: Record<string, any> = {};
    for (const t of txns.filter(t => t.merchantName)) {
      const key = t.merchantName!;
      if (!merchantMap[key]) merchantMap[key] = { merchantName: key, amount: 0, count: 0, category: t.category?.name ?? 'Other' };
      merchantMap[key].amount += Number(t.amount);
      merchantMap[key].count++;
    }
    const byMerchant = Object.values(merchantMap)
      .sort((a: any, b: any) => b.amount - a.amount)
      .slice(0, 15);

    // Daily trend
    const dailyMap: Record<string, any> = {};
    for (const t of txns) {
      const d = t.transactionDate.toISOString().split('T')[0];
      if (!dailyMap[d]) dailyMap[d] = { date: d, amount: 0, transactionCount: 0 };
      dailyMap[d].amount += Number(t.amount);
      dailyMap[d].transactionCount++;
    }
    const dailyTrend = Object.values(dailyMap).sort((a: any, b: any) => a.date.localeCompare(b.date));

    return {
      totalSpent:     total,
      averageDaily,
      averageWeekly:  averageDaily * 7,
      averageMonthly: averageDaily * 30,
      byCategory,
      byMerchant,
      byPaymentChannel: [],
      dailyTrend,
      weeklyTrend: [],
    };
  });

  res.json({ success: true, data });
}));

// ── GET /api/v1/analytics/income ────────────────────────────────────────────
router.get('/income', asyncHandler(async (req: AuthRequest, res) => {
  const { period } = periodSchema.parse(req.query);
  const userId     = req.user!.id;
  const { start, end } = getDateRange(period);

  const txns = await prisma.transaction.findMany({
    where: { userId, transactionDate: { gte: start, lte: end }, isTransfer: false, type: 'CREDIT' },
  });
  const total = txns.reduce((s, t) => s + Number(t.amount), 0);

  const sourceMap: Record<string, any> = {};
  for (const t of txns) {
    const src = t.source ?? 'MANUAL';
    if (!sourceMap[src]) sourceMap[src] = { source: src, amount: 0, count: 0 };
    sourceMap[src].amount += Number(t.amount);
    sourceMap[src].count++;
  }

  res.json({
    success: true,
    data: {
      totalIncome: total,
      bySource:    Object.values(sourceMap).map((s: any) => ({ ...s, percentage: total > 0 ? (s.amount / total) * 100 : 0 })),
      monthlyTrend: [],
    },
  });
}));

// ── GET /api/v1/analytics/cashflow ──────────────────────────────────────────
router.get('/cashflow', asyncHandler(async (req: AuthRequest, res) => {
  const { period } = periodSchema.parse(req.query);
  const userId     = req.user!.id;
  const { start, end } = getDateRange(period);

  const [accounts, txns] = await Promise.all([
    prisma.account.findMany({ where: { userId, isActive: true }, select: { balance: true } }),
    prisma.transaction.findMany({ where: { userId, transactionDate: { gte: start, lte: end }, isTransfer: false } }),
  ]);

  const currentBalance = accounts.reduce((s, a) => s + Number(a.balance), 0);
  const inflows  = txns.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
  const outflows = txns.filter(t => t.type === 'DEBIT' ).reduce((s, t) => s + Number(t.amount), 0);

  res.json({
    success: true,
    data: {
      openingBalance: currentBalance - inflows + outflows,
      closingBalance: currentBalance,
      netFlow:        inflows - outflows,
      inflows:        [],
      outflows:       [],
      dailyBalance:   [],
    },
  });
}));

// ── GET /api/v1/analytics/insights ──────────────────────────────────────────
router.get('/insights', asyncHandler(async (req: AuthRequest, res) => {
  const userId         = req.user!.id;
  const { start, end } = getDateRange('month');
  const prevEnd        = new Date(start.getTime() - 1);
  const prevStart      = new Date(start.getTime() - (end.getTime() - start.getTime()));

  const [currTxns, prevTxns, recurringTxns, budgets] = await Promise.all([
    prisma.transaction.findMany({ where: { userId, transactionDate: { gte: start, lte: end }, isTransfer: false }, include: { category: true } }),
    prisma.transaction.findMany({ where: { userId, transactionDate: { gte: prevStart, lte: prevEnd }, isTransfer: false }, include: { category: true } }),
    prisma.transaction.findMany({ where: { userId, isRecurring: true, type: 'DEBIT' }, orderBy: { transactionDate: 'desc' }, take: 20 }),
    prisma.budget.findMany({ where: { userId, isActive: true }, include: { category: true } }),
  ]);

  const insights: any[] = [];

  // 1. Category spending change
  const currBycat: Record<string, number> = {};
  const prevBycat: Record<string, number> = {};
  for (const t of currTxns.filter(t => t.type === 'DEBIT' && t.category)) {
    currBycat[t.category!.name] = (currBycat[t.category!.name] ?? 0) + Number(t.amount);
  }
  for (const t of prevTxns.filter(t => t.type === 'DEBIT' && t.category)) {
    prevBycat[t.category!.name] = (prevBycat[t.category!.name] ?? 0) + Number(t.amount);
  }
  for (const [cat, curr] of Object.entries(currBycat)) {
    const prev  = prevBycat[cat] ?? 0;
    if (prev > 0) {
      const pct = ((curr - prev) / prev) * 100;
      if (pct > 20) {
        insights.push({ id: `inc_${cat}`, type: 'SPENDING_INCREASE', title: `${cat} spending up`, description: `${cat} is ₹${(curr - prev).toFixed(0)} higher than last month (${pct.toFixed(0)}% increase)`, severity: pct > 50 ? 'WARNING' : 'INFO' });
      } else if (pct < -15) {
        insights.push({ id: `dec_${cat}`, type: 'SPENDING_DECREASE', title: `${cat} spending down`, description: `Good job! ${cat} dropped by ${Math.abs(pct).toFixed(0)}% vs last month`, severity: 'INFO' });
      }
    }
  }

  // 2. Upcoming subscriptions (within 7 days)
  const merchantLastSeen: Record<string, Date> = {};
  for (const t of recurringTxns) {
    const m = t.merchantName ?? t.description;
    if (!merchantLastSeen[m] || t.transactionDate > merchantLastSeen[m]) {
      merchantLastSeen[m] = t.transactionDate;
    }
  }
  for (const [merchant, lastDate] of Object.entries(merchantLastSeen)) {
    const nextDate = new Date(lastDate.getTime() + 30 * 24 * 60 * 60 * 1000);
    const daysLeft = Math.round((nextDate.getTime() - Date.now()) / 86_400_000);
    if (daysLeft >= 0 && daysLeft <= 7) {
      const amount = recurringTxns.find(t => (t.merchantName ?? t.description) === merchant);
      insights.push({
        id:          `sub_${merchant}`,
        type:        'SUBSCRIPTION_UPCOMING',
        title:       `${merchant} due ${daysLeft === 0 ? 'today' : `in ${daysLeft}d`}`,
        description: `₹${amount ? Number(amount.amount).toFixed(0) : '?'} subscription payment upcoming`,
        severity:    daysLeft <= 2 ? 'WARNING' : 'INFO',
      });
    }
  }

  // 3. Budget alerts
  for (const b of budgets) {
    const pct = Number(b.amount) > 0 ? (Number(b.spentAmount) / Number(b.amount)) * 100 : 0;
    if (pct >= 90) {
      insights.push({
        id:          `budget_${b.id}`,
        type:        'BUDGET_WARNING',
        title:       `${b.category.name} budget ${pct >= 100 ? 'exceeded' : 'almost full'}`,
        description: `₹${Number(b.spentAmount).toFixed(0)} of ₹${Number(b.amount).toFixed(0)} used (${pct.toFixed(0)}%)`,
        severity:    pct >= 100 ? 'CRITICAL' : 'WARNING',
      });
    }
  }

  // 4. Savings rate insight
  const currIncome  = currTxns.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
  const currExpense = currTxns.filter(t => t.type === 'DEBIT' ).reduce((s, t) => s + Number(t.amount), 0);
  if (currIncome > 0) {
    const savings = currIncome - currExpense;
    const rate    = (savings / currIncome) * 100;
    if (rate > 10) {
      insights.push({ id: 'savings_rate', type: 'SAVINGS_RATE', title: 'Great savings this month!', description: `You saved ₹${savings.toFixed(0)} (${rate.toFixed(0)}% savings rate)`, severity: 'INFO' });
    }
  }

  res.json({ success: true, data: insights.slice(0, 8) });
}));

// ── GET /api/v1/analytics/subscriptions ─────────────────────────────────────
router.get('/subscriptions', asyncHandler(async (req: AuthRequest, res) => {
  const userId = req.user!.id;

  const recurring = await prisma.transaction.findMany({
    where:   { userId, isRecurring: true, type: 'DEBIT' },
    orderBy: { transactionDate: 'desc' },
    take:    200,
  });

  // Group by merchant, keep average amount and most recent date
  const byMerchant: Record<string, { merchantName: string; amounts: number[]; lastDate: Date }> = {};
  for (const t of recurring) {
    const m = t.merchantName ?? t.description;
    if (!byMerchant[m]) byMerchant[m] = { merchantName: m, amounts: [], lastDate: t.transactionDate };
    byMerchant[m].amounts.push(Number(t.amount));
    if (t.transactionDate > byMerchant[m].lastDate) byMerchant[m].lastDate = t.transactionDate;
  }

  const subscriptions = Object.values(byMerchant).map(s => {
    const avg      = s.amounts.reduce((a, b) => a + b, 0) / s.amounts.length;
    const nextDate = new Date(s.lastDate.getTime() + 30 * 24 * 60 * 60 * 1000);
    return {
      merchant:        s.merchantName,
      amount:          avg,
      frequency:       'MONTHLY',
      nextPaymentDate: nextDate.toISOString(),
      annualCost:      avg * 12,
      status:          'ACTIVE',
    };
  }).sort((a, b) => new Date(a.nextPaymentDate).getTime() - new Date(b.nextPaymentDate).getTime());

  const totalMonthly = subscriptions.reduce((s, sub) => s + sub.amount, 0);

  res.json({ success: true, data: { subscriptions, totalMonthly, totalAnnual: totalMonthly * 12 } });
}));

// ── GET /api/v1/analytics/trends ────────────────────────────────────────────
router.get('/trends', asyncHandler(async (req: AuthRequest, res) => {
  const { months } = z.object({ months: z.coerce.number().int().min(1).max(24).default(6) }).parse(req.query);
  const userId     = req.user!.id;
  const result: any[] = [];

  for (let i = months - 1; i >= 0; i--) {
    const now   = new Date();
    const start = new Date(now.getFullYear(), now.getMonth() - i, 1);
    const end   = new Date(now.getFullYear(), now.getMonth() - i + 1, 0, 23, 59, 59);
    const label = start.toLocaleString('default', { month: 'short', year: '2-digit' });

    const txns = await prisma.transaction.findMany({
      where: { userId, transactionDate: { gte: start, lte: end }, isTransfer: false },
    });
    const income  = txns.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
    const expense = txns.filter(t => t.type === 'DEBIT' ).reduce((s, t) => s + Number(t.amount), 0);
    result.push({ month: label, income, expense, savings: income - expense });
  }

  res.json({ success: true, data: { monthlyTrend: result } });
}));

export { router as analyticsRouter };
