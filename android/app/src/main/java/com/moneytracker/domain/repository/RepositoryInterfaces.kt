package com.moneytracker.domain.repository

import com.moneytracker.domain.model.Account
import com.moneytracker.domain.model.Consent
import com.moneytracker.domain.model.Provider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface AccountRepository {
    fun getAccounts(userId: String): Flowable<List<Account>>
    fun getAccount(accountId: String): Maybe<Account>
    fun getActiveAccounts(userId: String): Flowable<List<Account>>
    fun getAccountsByConsent(consentId: String): Flowable<List<Account>>
    fun getTotalBalance(userId: String): Single<Money>
    fun insertAccount(account: Account): Completable
    fun insertAccounts(accounts: List<Account>): Completable
    fun updateAccount(account: Account): Completable
    fun updateAccountBalance(accountId: String, balance: Money, availableBalance: Money?, lastSynced: String): Completable
    fun setAccountSyncError(accountId: String, error: String): Completable
    fun syncAccount(accountId: String): Completable
    fun syncAccountFull(accountId: String): Completable
    fun deleteAccount(accountId: String): Completable
    fun getPendingSyncAccounts(userId: String): Flowable<List<Account>>
}

interface ConsentRepository {
    fun getConsents(userId: String): Flowable<List<Consent>>
    fun getActiveConsents(userId: String): Flowable<List<Consent>>
    fun getConsent(consentId: String): Maybe<Consent>
    fun getConsentByConsentId(consentId: String): Maybe<Consent>
    fun getConsentsByProvider(userId: String, providerId: String): Flowable<List<Consent>>
    fun insertConsent(consent: Consent): Completable
    fun updateConsent(consent: Consent): Completable
    fun revokeConsent(consentId: String): Completable
    fun getAvailableProviders(): Single<List<Provider>>
    fun initiateConsent(providerId: String, accounts: List<String>, permissions: List<String>): Single<String> // Returns consent URL
    fun handleConsentCallback(consentId: String, code: String): Single<Consent>
}

interface TransactionRepository {
    fun getTransactions(
        userId: String,
        filter: TransactionFilter,
        page: Int,
        limit: Int
    ): Flowable<PaginatedResult<Transaction>>

    fun getTransaction(transactionId: String): Maybe<Transaction>

    fun getTransactionsByDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flowable<List<Transaction>>

    fun getTransactionsByCategory(userId: String, categoryId: String): Flowable<List<Transaction>>

    fun getTransactionsByAccount(userId: String, accountId: String, page: Int, limit: Int): Flowable<PaginatedResult<Transaction>>

    fun getTransfers(userId: String): Flowable<List<Transaction>>

    fun getRecurringTransactions(userId: String): Flowable<List<Transaction>>

    fun searchTransactions(userId: String, query: String, limit: Int): Flowable<List<Transaction>>

    fun getTotalIncome(userId: String, startDate: String, endDate: String): Single<Money>

    fun getTotalExpense(userId: String, startDate: String, endDate: String): Single<Money>

    fun getCategorySpending(userId: String, startDate: String, endDate: String): Flowable<List<CategorySpending>>

    fun insertTransaction(transaction: Transaction): Completable

    fun insertTransactions(transactions: List<Transaction>): Completable

    fun updateTransaction(transaction: Transaction): Completable

    fun deleteTransaction(transactionId: String): Completable

    fun deleteOldTransactions(userId: String, cutoffDate: String): Completable

    fun getTransactionCount(userId: String): Single<Int>
}

data class TransactionFilter(
    val type: TransactionType? = null,
    val categoryId: String? = null,
    val accountId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val search: String? = null,
    val isRecurring: Boolean? = null,
    val isTransfer: Boolean? = null,
    val sortBy: SortBy = SortBy.DATE,
    val sortOrder: SortOrder = SortOrder.DESC
)

enum class SortBy { DATE, AMOUNT, MERCHANT, CATEGORY }
enum class SortOrder { ASC, DESC }

data class PaginatedResult<T>(
    val items: List<T>,
    val page: Int,
    val limit: Int,
    val total: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

data class CategorySpending(
    val categoryId: String,
    val total: Money
)