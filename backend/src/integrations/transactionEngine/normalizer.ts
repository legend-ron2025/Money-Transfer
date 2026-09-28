// Normalises raw transaction data from any source into a consistent internal format

export interface RawTransaction {
  externalId?: string | null;
  amount: number;
  currency?: string | null;
  type?: string | null;     // 'DEBIT' | 'CREDIT'
  description: string;
  transactionDate: string | Date;
  upiReference?: string | null;
  bankReference?: string | null;
  source: string;
  accountId: string;
  userId: string;
}

export interface NormalizedTransaction extends RawTransaction {
  amount: number;               // Always positive
  type: 'DEBIT' | 'CREDIT';
  currency: string;
  description: string;          // Trimmed, max 500 chars
  extractedUpiRef: string | null;
  extractedMerchant: string | null;
  normalizedDate: Date;
  paymentChannel: string;
}

// UPI reference: UPI/123456789012/Merchant Name/BANKCODE
const UPI_REF_RE        = /UPI[\/\s\-](\d{12,15})/i;
const UPI_MERCHANT_RE   = /UPI\/\d+\/([^\/]{2,50})\//i;
const AMOUNT_RE         = /(?:Rs\.?|INR|₹)\s*([\d,]+(?:\.\d{1,2})?)/i;

export function normalizeTransaction(raw: RawTransaction): NormalizedTransaction {
  // Amount is always stored positive; type determines direction
  const amount = Math.abs(raw.amount);

  // Infer type from sign when not provided
  let type: 'DEBIT' | 'CREDIT' =
    (raw.type?.toUpperCase() as 'DEBIT' | 'CREDIT') ||
    (raw.amount < 0 ? 'DEBIT' : 'CREDIT');
  if (type !== 'DEBIT' && type !== 'CREDIT') type = 'DEBIT';

  const currency = ((raw.currency || 'INR').trim().toUpperCase()).substring(0, 3);

  const description = (raw.description || '')
    .trim()
    .replace(/\s+/g, ' ')
    .substring(0, 500);

  const upiMatch       = description.match(UPI_REF_RE);
  const extractedUpiRef = raw.upiReference || (upiMatch ? upiMatch[1] : null);

  const merchantMatch   = description.match(UPI_MERCHANT_RE);
  const rawMerchant     = merchantMatch ? merchantMatch[1] : null;
  const extractedMerchant = rawMerchant
    ? rawMerchant.replace(/[_\-]+/g, ' ').trim()
    : null;

  let normalizedDate: Date;
  try {
    normalizedDate = raw.transactionDate instanceof Date
      ? raw.transactionDate
      : new Date(raw.transactionDate);
    if (isNaN(normalizedDate.getTime())) normalizedDate = new Date();
  } catch {
    normalizedDate = new Date();
  }

  return {
    ...raw,
    amount,
    type,
    currency,
    description,
    extractedUpiRef,
    extractedMerchant,
    normalizedDate,
    paymentChannel: extractPaymentChannel(description),
  };
}

export function extractPaymentChannel(description: string): string {
  const d = description.toUpperCase();
  if (d.includes('UPI') || d.match(/\/[A-Z]{3,8}@/)) return 'UPI';
  if (d.includes('NEFT'))                               return 'NEFT';
  if (d.includes('IMPS'))                               return 'IMPS';
  if (d.includes('RTGS'))                               return 'RTGS';
  if (d.includes('POS') || d.includes('CARD'))          return 'CARD';
  if (d.includes('ATM'))                                return 'CASH';
  if (d.includes('WALLET') || d.includes('PAYTM'))      return 'WALLET';
  if (d.includes('NETBANKING') || d.includes('INB'))    return 'NET_BANKING';
  return 'OTHER';
}
