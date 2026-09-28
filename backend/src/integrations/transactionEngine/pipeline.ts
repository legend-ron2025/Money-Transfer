import { randomUUID } from 'crypto';
import { normalizeTransaction, extractPaymentChannel, RawTransaction } from './normalizer.js';
import { identifyMerchant } from './merchantIdentifier.js';
import { categorizeTransaction } from './categorizer.js';
import { isDuplicate } from './deduplicator.js';
import { prisma } from '../../index.js';

export interface PipelineResult {
  inserted: number;
  duplicates: number;
  errors: string[];
}

/** Full transaction processing pipeline:
 *  normalize → deduplicate → identify merchant → categorize → store */
export async function processTransactions(raws: RawTransaction[]): Promise<PipelineResult> {
  let inserted = 0;
  let duplicates = 0;
  const errors: string[] = [];

  for (const raw of raws) {
    try {
      // Step 1 — Normalize
      const n = normalizeTransaction(raw);

      // Step 2 — Deduplicate
      const dup = await isDuplicate({
        externalId:      raw.externalId,
        amount:          n.amount,
        type:            n.type,
        transactionDate: n.normalizedDate,
        upiReference:    n.extractedUpiRef,
        accountId:       raw.accountId,
        userId:          raw.userId,
      });
      if (dup) { duplicates++; continue; }

      // Step 3 — Identify merchant
      const merchant = identifyMerchant(n.description, n.extractedMerchant);

      // Step 4 — Categorize
      const cat = await categorizeTransaction(
        {
          description:      n.description,
          merchantName:     merchant?.name ?? null,
          extractedMerchant: n.extractedMerchant,
          amount:           n.amount,
          type:             n.type,
          paymentChannel:   n.paymentChannel,
        },
        raw.userId,
      );

      // Step 5 — Persist
      await prisma.transaction.create({
        data: {
          id:              randomUUID(),
          userId:          raw.userId,
          accountId:       raw.accountId,
          externalId:      raw.externalId ?? null,
          amount:          n.amount,
          currency:        n.currency,
          type:            n.type,
          status:          'COMPLETED',
          description:     n.description,
          merchantName:    merchant?.name ?? n.extractedMerchant ?? null,
          merchantLogo:    merchant?.logo ?? null,
          transactionDate: n.normalizedDate,
          categoryId:      cat.categoryId ?? null,
          subcategory:     cat.subcategoryName ?? null,
          source:          raw.source,
          paymentChannel:  n.paymentChannel !== 'OTHER' ? n.paymentChannel : null,
          upiReference:    n.extractedUpiRef ?? null,
          bankReference:   raw.bankReference ?? null,
          isRecurring:     false,
          isTransfer:      false,
          confidence:      cat.confidence,
          tags:            [],
        },
      });
      inserted++;
    } catch (err) {
      errors.push(err instanceof Error ? err.message : String(err));
    }
  }

  return { inserted, duplicates, errors };
}
