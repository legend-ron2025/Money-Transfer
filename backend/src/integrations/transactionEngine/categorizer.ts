import { identifyMerchant } from './merchantIdentifier.js';
import { prisma } from '../../index.js';

export interface TransactionForCategorization {
  description: string;
  merchantName?: string | null;
  extractedMerchant?: string | null;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  paymentChannel?: string | null;
}

export interface CategorizationResult {
  categoryName: string;
  subcategoryName?: string;
  confidence: number;
  categoryId?: string;
}

// Keyword rules — checked against uppercased description when merchant lookup fails
const KEYWORD_RULES: Array<{ keywords: string[]; category: string; subcategory?: string }> = [
  { keywords: ['SALARY', 'PAYROLL', 'WAGES', 'STIPEND'],                  category: 'Salary' },
  { keywords: ['INTEREST', 'INT CREDIT', 'SAVINGS INT', 'INT ON SAVINGS'], category: 'Interest' },
  { keywords: ['REFUND', 'CASHBACK', 'REVERSAL', 'CHARGEBACK'],            category: 'Cashback' },
  { keywords: ['ATM', 'CASH WITHDRAWAL', 'CASH WDR', 'ATW'],               category: 'Other',      subcategory: 'Cash Withdrawal' },
  { keywords: ['EMI', 'INSTALMENT', 'LOAN REPAY', 'HOME LOAN', 'CAR LOAN'],category: 'EMI/Loans' },
  { keywords: ['INSURANCE', 'POLICY', 'PREMIUM', 'LIFE INS'],              category: 'Insurance' },
  { keywords: ['MUTUAL FUND', 'SIP', 'MF', 'DEMAT', 'EQUITY', 'STOCK'],   category: 'Investments' },
  { keywords: ['ELECTRICITY', 'POWER BILL', 'MSEDCL', 'BESCOM', 'ELECTRIC'],category: 'Bills',    subcategory: 'Electricity' },
  { keywords: ['BROADBAND', 'INTERNET', 'WIFI', 'FIBERNET'],               category: 'Bills',      subcategory: 'Internet' },
  { keywords: ['RECHARGE', 'MOBILE RECHARGE', 'PREPAID', 'POSTPAID'],      category: 'Bills',      subcategory: 'Mobile' },
  { keywords: ['WATER BILL', 'WATER SUPPLY'],                               category: 'Bills',      subcategory: 'Water' },
  { keywords: ['GROCERY', 'KIRANA', 'SUPERMARKET', 'VEGETABLES', 'MARKET'],category: 'Food',       subcategory: 'Groceries' },
  { keywords: ['RESTAURANT', 'HOTEL FOOD', 'DINING', 'CAFE', 'EATERY'],    category: 'Food',       subcategory: 'Restaurants' },
  { keywords: ['SCHOOL', 'COLLEGE', 'UNIVERSITY', 'TUITION', 'COURSE'],    category: 'Education' },
  { keywords: ['HOSPITAL', 'CLINIC', 'MEDICAL', 'PHARMACY', 'MEDICINE'],   category: 'Health' },
  { keywords: ['FLIGHT', 'AIRLINES', 'AIRWAYS', 'AVIATION'],               category: 'Travel',     subcategory: 'Flights' },
  { keywords: ['HOTEL', 'RESORT', 'LODGING', 'STAY'],                      category: 'Travel',     subcategory: 'Hotels' },
  { keywords: ['FUEL', 'PETROL', 'DIESEL', 'CNG', 'HP GAS', 'PETROPUMP'], category: 'Transport',   subcategory: 'Fuel' },
  { keywords: ['FASTAG', 'TOLL', 'PARKING', 'MUNICIPAL PARKING'],          category: 'Transport',  subcategory: 'Parking' },
  { keywords: ['METRO', 'BUS PASS', 'IRCTC', 'TRAIN', 'KSRTC', 'BMTC'],  category: 'Transport',  subcategory: 'Public Transport' },
  { keywords: ['NEFT', 'IMPS', 'RTGS', 'TRANSFER TO', 'SENT TO', 'SELF'], category: 'Transfer' },
];

// Cache: categoryName -> categoryId  (per-process, cleared on restart)
const categoryIdCache = new Map<string, string | undefined>();

async function resolveCategoryId(name: string, userId: string): Promise<string | undefined> {
  const cacheKey = `${name}::${userId}`;
  if (categoryIdCache.has(cacheKey)) return categoryIdCache.get(cacheKey);

  const cat = await prisma.category.findFirst({
    where: {
      name,
      OR: [{ userId: null }, { userId }],
    },
    select: { id: true },
  });
  const id = cat?.id;
  categoryIdCache.set(cacheKey, id);
  return id;
}

export async function categorizeTransaction(
  txn: TransactionForCategorization,
  userId: string,
): Promise<CategorizationResult> {
  const descUpper = txn.description.toUpperCase();

  // 1. Merchant identification (highest confidence)
  const match = identifyMerchant(txn.description, txn.extractedMerchant ?? txn.merchantName);
  if (match && match.confidence >= 0.8 && match.category !== 'Other') {
    const subName = match.subcategory ?? match.category;
    const catId = await resolveCategoryId(subName, userId)
      ?? await resolveCategoryId(match.category, userId);
    return {
      categoryName:    match.category,
      subcategoryName: match.subcategory,
      confidence:      match.confidence,
      categoryId:      catId,
    };
  }

  // 2. Keyword rules
  for (const rule of KEYWORD_RULES) {
    if (rule.keywords.some(kw => descUpper.includes(kw))) {
      const subName = rule.subcategory ?? rule.category;
      const catId = await resolveCategoryId(subName, userId)
        ?? await resolveCategoryId(rule.category, userId);
      return {
        categoryName:    rule.category,
        subcategoryName: rule.subcategory,
        confidence:      0.7,
        categoryId:      catId,
      };
    }
  }

  // 3. Income fallback
  if (txn.type === 'CREDIT') {
    return {
      categoryName: 'Other Income',
      confidence:   0.5,
      categoryId:   await resolveCategoryId('Other Income', userId),
    };
  }

  // 4. Default
  return {
    categoryName: 'Other',
    confidence:   0.3,
    categoryId:   await resolveCategoryId('Other', userId),
  };
}
