/**
 * Maps Setu / RBI FI schema to our internal Transaction model.
 * Setu FI schema reference: https://docs.setu.co/data/account-aggregator/fi-data-types
 */

export interface FITransaction {
  txnId:                string;
  type:                 'CREDIT' | 'DEBIT';
  mode:                 string;        // UPI, NEFT, IMPS, CARD, etc.
  amount:               number;
  currentBalance:       number;
  transactionTimestamp: string;        // ISO 8601
  valueDate:            string;
  narration:            string;
  reference?:           string;
}

export interface FIAccount {
  maskedAccountNumber: string;
  ifsc?:               string;
  accountType?:        string;
  transactions:        FITransaction[];
}

export interface MappedTransaction {
  externalId:      string;
  amount:          number;
  type:            'DEBIT' | 'CREDIT';
  description:     string;
  transactionDate: string;
  bankReference:   string | null;
  source:          'AA';
}

/** Map one FI account's transactions to our internal format */
export function mapFITransactions(
  fiAccount: FIAccount,
  accountId: string,
  userId: string,
): Array<MappedTransaction & { accountId: string; userId: string }> {
  return fiAccount.transactions.map(t => ({
    externalId:      t.txnId,
    amount:          Math.abs(t.amount),
    type:            t.type,
    description:     sanitizeNarration(t.narration),
    transactionDate: t.transactionTimestamp,
    bankReference:   t.reference ?? null,
    source:          'AA' as const,
    accountId,
    userId,
  }));
}

function sanitizeNarration(n: string): string {
  return (n ?? '').trim().replace(/\s+/g, ' ').substring(0, 500);
}

/** Parse Setu's encrypted FI data payload (already decrypted by jweDecryptor) */
export function parseFIPayload(payload: unknown): FIAccount[] {
  if (!payload || typeof payload !== 'object') return [];
  const p = payload as any;

  // Setu wraps data as: { fiObjects: [ { data: { account: { transactions: { transaction: [...] } } } } ] }
  const fiObjects = Array.isArray(p.fiObjects) ? p.fiObjects : [p];

  return fiObjects.map((obj: any) => {
    const acc  = obj?.data?.account ?? obj?.account ?? {};
    const txns = acc?.transactions?.transaction ?? acc?.transactions ?? [];

    return {
      maskedAccountNumber: acc?.maskedAccountNumber ?? acc?.linkedAccRef ?? '',
      ifsc:                acc?.ifscCode ?? null,
      accountType:         acc?.accountType ?? 'SAVINGS',
      transactions:        (Array.isArray(txns) ? txns : [txns]).map((t: any) => ({
        txnId:                t.txnId        ?? t.transactionId ?? crypto.randomUUID(),
        type:                 (t.type        ?? 'DEBIT').toUpperCase(),
        mode:                 t.mode         ?? 'OTHER',
        amount:               parseFloat(t.amount ?? '0'),
        currentBalance:       parseFloat(t.currentBalance ?? '0'),
        transactionTimestamp: t.transactionTimestamp ?? t.valueDate ?? new Date().toISOString(),
        valueDate:            t.valueDate    ?? t.transactionTimestamp ?? new Date().toISOString(),
        narration:            t.narration    ?? t.remarks ?? '',
        reference:            t.reference   ?? t.referenceNumber ?? null,
      })),
    } as FIAccount;
  });
}
