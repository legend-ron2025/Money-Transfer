package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    val accountId: String,
    val account: AccountSummary?,
    val externalId: String?,
    val amount: Money,
    val type: TransactionType,
    val status: TransactionStatus,
    val description: String,
    val merchantName: String?,
    val merchantLogo: String?,
    val transactionDate: String,
    val postedDate: String?,
    val category: Category?,
    val subcategory: String?,
    val source: TransactionSource,
    val paymentChannel: PaymentChannel?,
    val upiReference: String?,
    val bankReference: String?,
    val isRecurring: Boolean,
    val isTransfer: Boolean,
    val transferPairId: String?,
    val confidence: Double,
    val notes: String?,
    val tags: List<String>,
    val location: Location?,
    val createdAt: String,
    val updatedAt: String
) {
    val isIncome: Boolean
        get() = type == TransactionType.CREDIT

    val isExpense: Boolean
        get() = type == TransactionType.DEBIT

    val isTransfer: Boolean
        get() = type == TransactionType.TRANSFER

    val displayAmount: Money
        get() = when (type) {
            TransactionType.CREDIT -> amount
            TransactionType.DEBIT -> Money(-amount.amount, amount.currency)
            TransactionType.TRANSFER -> Money(0.0, amount.currency)
        }
}

@Serializable
data class AccountSummary(
    val id: String,
    val maskedNumber: String,
    val institutionName: String,
    val accountType: Account.AccountType
)

@Serializable
data class Location(
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
)

enum class TransactionType {
    DEBIT, CREDIT, TRANSFER
}

enum class TransactionStatus {
    PENDING, COMPLETED, FAILED, CANCELLED
}

enum class TransactionSource {
    BANK, AA, MANUAL, UPI, CARD, WALLET
}

enum class PaymentChannel {
    UPI, NET_BANKING, CARD, WALLET, CASH, IMPS, NEFT, RTGS, OTHER
}