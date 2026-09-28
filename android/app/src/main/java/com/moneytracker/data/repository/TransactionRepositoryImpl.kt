package com.moneytracker.data.repository

import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.TransactionDao
import com.moneytracker.core.database.entity.TransactionEntity
import com.moneytracker.data.mapper.TransactionMapper
import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.TransactionRepository
import com.moneytracker.domain.repository.TransactionFilter
import com.moneytracker.domain.repository.PaginatedResult
import com.moneytracker.domain.repository.CategorySpending
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val transactionDao: TransactionDao,
    private val transactionsApi: com.moneytracker.core.network.api.TransactionsApi
) : TransactionRepository {

    override fun getTransactions(
        userId: String,
        filter: TransactionFilter,
        page: Int,
        limit: Int
    ): Flowable<PaginatedResult<Transaction>> {
        val offset = (page - 1) * limit
        
        return when {
            filter.type != null -> transactionDao.getTransactionsByType(userId, filter.type!!.name, limit, offset)
            filter.categoryId != null -> transactionDao.getTransactionsByCategory(userId, filter.categoryId!!)
            filter.accountId != null -> transactionDao.getTransactionsByAccount(userId, filter.accountId!!, limit, offset)
            filter.startDate != null && filter.endDate != null -> transactionDao.getTransactionsByDateRange(userId, filter.startDate!!, filter.endDate!!)
            else -> transactionDao.getTransactions(userId, limit, offset)
        }.map { entities ->
            val items = entities.map { TransactionMapper.toDomain(it) }
            PaginatedResult(
                items = items,
                page = page,
                limit = limit,
                total = items.size.toLong(),
                totalPages = 1,
                hasNext = items.size == limit,
                hasPrevious = page > 1
            )
        }
    }

    override fun getTransaction(transactionId: String): Maybe<Transaction> {
        return transactionDao.getTransaction(transactionId)
            .map { TransactionMapper.toDomain(it) }
    }

    override fun getTransactionsByDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flowable<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
            .map { entities -> entities.map { TransactionMapper.toDomain(it) } }
    }

    override fun getTransactionsByCategory(userId: String, categoryId: String): Flowable<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(userId, categoryId)
            .map { entities -> entities.map { TransactionMapper.toDomain(it) } }
    }

    override fun getTransactionsByAccount(userId: String, accountId: String, page: Int, limit: Int): Flowable<PaginatedResult<Transaction>> {
        val offset = (page - 1) * limit
        return transactionDao.getTransactionsByAccount(userId, accountId, limit, offset)
            .map { entities ->
                val items = entities.map { TransactionMapper.toDomain(it) }
                PaginatedResult(
                    items = items,
                    page = page,
                    limit = limit,
                    total = items.size.toLong(),
                    totalPages = 1,
                    hasNext = items.size == limit,
                    hasPrevious = page > 1
                )
            }
    }

    override fun getTransfers(userId: String): Flowable<List<Transaction>> {
        return transactionDao.getTransfers(userId)
            .map { entities -> entities.map { TransactionMapper.toDomain(it) } }
    }

    override fun getRecurringTransactions(userId: String): Flowable<List<Transaction>> {
        return transactionDao.getRecurringTransactions(userId)
            .map { entities -> entities.map { TransactionMapper.toDomain(it) } }
    }

    override fun searchTransactions(userId: String, query: String, limit: Int): Flowable<List<Transaction>> {
        return transactionDao.searchTransactions(userId, "%$query%", limit)
            .map { entities -> entities.map { TransactionMapper.toDomain(it) } }
    }

    override fun getTotalIncome(userId: String, startDate: String, endDate: String): Single<Money> {
        return transactionDao.getTotalIncome(userId, startDate, endDate)
            .map { Money(it ?: 0.0) }
            .subscribeOn(Schedulers.io())
    }

    override fun getTotalExpense(userId: String, startDate: String, endDate: String): Single<Money> {
        return transactionDao.getTotalExpense(userId, startDate, endDate)
            .map { Money(it ?: 0.0) }
            .subscribeOn(Schedulers.io())
    }

    override fun getCategorySpending(userId: String, startDate: String, endDate: String): Flowable<List<CategorySpending>> {
        return transactionDao.getCategorySpending(userId, startDate, endDate)
            .map { list ->
                list.map { CategorySpending(it.category_id, Money(it.total)) }
            }
    }

    override fun insertTransaction(transaction: Transaction): Completable {
        return Completable.fromAction {
            val entity = toEntity(transaction)
            transactionDao.insertTransaction(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertTransactions(transactions: List<Transaction>): Completable {
        return Completable.fromAction {
            val entities = transactions.map { toEntity(it) }
            transactionDao.insertTransactions(entities)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateTransaction(transaction: Transaction): Completable {
        return Completable.fromAction {
            val entity = toEntity(transaction)
            transactionDao.updateTransaction(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTransaction(transactionId: String): Completable {
        return Completable.fromAction {
            transactionDao.deleteTransaction(transactionId)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteOldTransactions(userId: String, cutoffDate: String): Completable {
        return Completable.fromAction {
            transactionDao.deleteOldTransactions(userId, cutoffDate)
        }.subscribeOn(Schedulers.io())
    }

    override fun getTransactionCount(userId: String): Single<Int> {
        return transactionDao.getTransactionCount(userId)
            .subscribeOn(Schedulers.io())
    }

    private fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            userId = transaction.userId,
            accountId = transaction.accountId,
            externalId = transaction.externalId,
            amount = transaction.amount.amount,
            currency = transaction.amount.currency,
            type = transaction.type.name,
            status = transaction.status.name,
            description = transaction.description,
            merchantName = transaction.merchantName,
            merchantLogo = transaction.merchantLogo,
            transactionDate = transaction.transactionDate,
            postedDate = transaction.postedDate,
            categoryId = transaction.category?.id,
            subcategory = transaction.subcategory,
            source = transaction.source.name,
            paymentChannel = transaction.paymentChannel?.name,
            upiReference = transaction.upiReference,
            bankReference = transaction.bankReference,
            isRecurring = transaction.isRecurring,
            isTransfer = transaction.isTransfer,
            transferPairId = transaction.transferPairId,
            confidence = transaction.confidence,
            notes = transaction.notes,
            tags = com.google.gson.Gson().toJson(transaction.tags),
            locationLatitude = transaction.location?.latitude,
            locationLongitude = transaction.location?.longitude,
            locationAddress = transaction.location?.address,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }
}