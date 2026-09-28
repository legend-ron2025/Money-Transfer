package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.TransactionRepository
import com.moneytracker.domain.repository.TransactionFilter
import com.moneytracker.domain.repository.PaginatedResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        userId: String,
        filter: TransactionFilter,
        page: Int = 1,
        limit: Int = 50
    ): Flowable<PaginatedResult<Transaction>> {
        return transactionRepository.getTransactions(userId, filter, page, limit)
    }
}

class GetTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(transactionId: String): Maybe<Transaction> {
        return transactionRepository.getTransaction(transactionId)
    }
}

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, startDate: String, endDate: String): Flowable<List<Transaction>> {
        return transactionRepository.getTransactionsByDateRange(userId, startDate, endDate)
    }
}

class GetTransactionsByCategoryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, categoryId: String): Flowable<List<Transaction>> {
        return transactionRepository.getTransactionsByCategory(userId, categoryId)
    }
}

class GetTransactionsByAccountUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, accountId: String, page: Int = 1, limit: Int = 50): Flowable<PaginatedResult<Transaction>> {
        return transactionRepository.getTransactionsByAccount(userId, accountId, page, limit)
    }
}

class GetTransfersUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String): Flowable<List<Transaction>> {
        return transactionRepository.getTransfers(userId)
    }
}

class GetRecurringTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String): Flowable<List<Transaction>> {
        return transactionRepository.getRecurringTransactions(userId)
    }
}

class SearchTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, query: String, limit: Int = 20): Flowable<List<Transaction>> {
        return transactionRepository.searchTransactions(userId, query, limit)
    }
}

class GetTotalIncomeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, startDate: String, endDate: String): Single<Money> {
        return transactionRepository.getTotalIncome(userId, startDate, endDate)
    }
}

class GetTotalExpenseUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, startDate: String, endDate: String): Single<Money> {
        return transactionRepository.getTotalExpense(userId, startDate, endDate)
    }
}

class GetCategorySpendingUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(userId: String, startDate: String, endDate: String): Flowable<List<CategorySpending>> {
        return transactionRepository.getCategorySpending(userId, startDate, endDate)
    }
}

class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(transaction: Transaction): Completable {
        return transactionRepository.insertTransaction(transaction)
    }
}

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(transaction: Transaction): Completable {
        return transactionRepository.updateTransaction(transaction)
    }
}

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(transactionId: String): Completable {
        return transactionRepository.deleteTransaction(transactionId)
    }
}

class BulkUpdateTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(transactions: List<Transaction>): Completable {
        return transactionRepository.insertTransactions(transactions)
    }
}