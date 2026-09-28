package com.moneytracker.core.sms

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SmsTransaction(
    val amount: Double,
    val type: String,           // "DEBIT" | "CREDIT"
    val maskedAccount: String?,
    val merchant: String?,
    val upiRef: String?,
    val availableBalance: Double?,
    val transactionDate: Date,
    val source: String = "SMS",
    val rawSender: String = ""
)

object SmsParser {

    // Bank sender IDs to accept
    val BANK_SENDERS = setOf(
        "HDFCBK", "HDFC", "ICICIB", "ICICI", "SBIINB", "SBISMS",
        "AXISBK", "AXIS", "KOTAKB", "KOTAK", "YESBK", "YESBNK",
        "IDFCBK", "IDFC", "PNBSMS", "PAYTM", "BOBTXN", "FEDERAL",
        "INDBNK", "CANBNK", "UNIONB"
    )

    fun isBankSender(sender: String): Boolean =
        BANK_SENDERS.any { sender.uppercase().contains(it) }

    // ── Regex patterns ──────────────────────────────────────────────────────
    private val AMOUNT_RE      = Regex("""(?:INR|Rs\.?|₹)\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
    private val DEBIT_WORDS    = Regex("""(?:debited|debit|paid|spent|sent|withdrawal|withdrawn|payment\s+of|transferred\s+from)""", RegexOption.IGNORE_CASE)
    private val CREDIT_WORDS   = Regex("""(?:credited|credit|received|refund|cashback|deposited|added)""", RegexOption.IGNORE_CASE)
    private val ACCOUNT_RE     = Regex("""(?:a/?c|acct?|account)\s*(?:no\.?\s*)?[Xx*]{0,6}(\d{4})""", RegexOption.IGNORE_CASE)
    private val UPI_REF_RE     = Regex("""(?:UPI\s*ref(?:erence)?|UPI\s*ID|ref\s*no\.?)\s*:?\s*([A-Za-z0-9]{10,25})""", RegexOption.IGNORE_CASE)
    private val UPI_ID_RE      = Regex("""UPI[\/\s\-](\d{12,18})""", RegexOption.IGNORE_CASE)
    private val MERCHANT_UPI   = Regex("""(?:to|at|at merchant|VPA)\s+([A-Za-z0-9@.\-_]{3,40})""", RegexOption.IGNORE_CASE)
    private val BALANCE_RE     = Regex("""(?:Avl\.?\s*Bal|Available\s+Balance|Bal(?:ance)?)\s*:?\s*(?:INR|Rs\.?|₹)?\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
    private val OTP_RE         = Regex("""(?:OTP|one[- ]time password|passcode)\s*(?:is|:)?\s*\d{4,8}""", RegexOption.IGNORE_CASE)

    fun parseSms(sender: String, body: String): SmsTransaction? {
        // Skip OTP messages
        if (OTP_RE.containsMatchIn(body)) return null
        // Skip promotional / non-transactional
        if (!AMOUNT_RE.containsMatchIn(body)) return null
        if (!DEBIT_WORDS.containsMatchIn(body) && !CREDIT_WORDS.containsMatchIn(body)) return null

        val amountStr = AMOUNT_RE.find(body)?.groupValues?.get(1)?.replace(",", "") ?: return null
        val amount    = amountStr.toDoubleOrNull() ?: return null
        if (amount <= 0) return null

        val type = when {
            DEBIT_WORDS.containsMatchIn(body)  -> "DEBIT"
            CREDIT_WORDS.containsMatchIn(body) -> "CREDIT"
            else -> return null
        }

        val maskedAccount    = ACCOUNT_RE.find(body)?.groupValues?.get(1)
        val upiRef           = UPI_REF_RE.find(body)?.groupValues?.get(1) ?: UPI_ID_RE.find(body)?.groupValues?.get(1)
        val merchant         = MERCHANT_UPI.find(body)?.groupValues?.get(1)?.trim()?.takeIf { it.length >= 3 }
        val availableBalance = BALANCE_RE.find(body)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        return SmsTransaction(
            amount           = amount,
            type             = type,
            maskedAccount    = maskedAccount,
            merchant         = merchant,
            upiRef           = upiRef,
            availableBalance = availableBalance,
            transactionDate  = Date(),
            source           = "SMS",
            rawSender        = sender
        )
    }
}
