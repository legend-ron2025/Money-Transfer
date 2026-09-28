package com.moneytracker.data.repository

import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.AccountDao
import com.moneytracker.core.database.entity.AccountEntity
import com.moneytracker.data.mapper.AccountMapper
import com.moneytracker.domain.model.Account
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.AccountRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val accountDao: AccountDao,
    private val accountsApi: com.moneytracker.core.network.api.AccountsApi
) : AccountRepository {

    override fun getAccounts(userId: String): Flowable<List<Account>> {
        return accountDao.getAllAccounts(userId)
            .map { entities -> entities.map { AccountMapper.toDomain(it) } }
    }

    override fun getAccount(accountId: String): Maybe<Account> {
        return accountDao.getAccount(accountId)
            .map { AccountMapper.toDomain(it) }
    }

    override fun getActiveAccounts(userId: String): Flowable<List<Account>> {
        return accountDao.getActiveAccounts(userId)
            .map { entities -> entities.map { AccountMapper.toDomain(it) } }
    }

    override fun getAccountsByConsent(consentId: String): Flowable<List<Account>> {
        return accountDao.getAccountsByConsent(consentId)
            .map { entities -> entities.map { AccountMapper.toDomain(it) } }
    }

    override fun getTotalBalance(userId: String): Single<Money> {
        return accountDao.getTotalBalance(userId)
            .map { Money(it ?: 0.0) }
            .subscribeOn(Schedulers.io())
    }

    override fun insertAccount(account: Account): Completable {
        return Completable.fromAction {
            val entity = toEntity(account)
            accountDao.insertAccount(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertAccounts(accounts: List<Account>): Completable {
        return Completable.fromAction {
            val entities = accounts.map { toEntity(it) }
            accountDao.insertAccounts(entities)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateAccount(account: Account): Completable {
        return Completable.fromAction {
            val entity = toEntity(account)
            accountDao.updateAccount(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateAccountBalance(
        accountId: String,
        balance: Money,
        availableBalance: Money?,
        lastSynced: String
    ): Completable {
        return Completable.fromAction {
            accountDao.updateAccountBalance(
                accountId,
                balance.amount,
                availableBalance?.amount,
                lastSynced,
                "SYNCED"
            )
        }.subscribeOn(Schedulers.io())
    }

    override fun setAccountSyncError(accountId: String, error: String): Completable {
        return Completable.fromAction {
            accountDao.setAccountSyncError(accountId, error)
        }.subscribeOn(Schedulers.io())
    }

    override fun syncAccount(accountId: String): Completable {
        return Completable.fromAction {
            // This would trigger a sync with the backend
            // For now, we just update the sync status
            accountDao.updateAccount(accountId, "SYNCING")
        }.subscribeOn(Schedulers.io())
            .flatMapCompletable {
                accountsApi.syncAccount(accountId)
                    .flatMapCompletable { response ->
                        if (response.success && response.data != null) {
                            val data = response.data!!
                            return@flatMapCompletable updateAccountBalance(
                                accountId,
                                Money(data.balance, "INR"),
                                data.availableBalance?.let { Money(it, "INR") },
                                data.lastSyncedAt ?: java.time.Instant.now().toString()
                            )
                        } else {
                            return@flatMapCompletable setAccountSyncError(
                                accountId,
                                response.error?.message ?: "Sync failed"
                            )
                        }
                    }
                    .onErrorResumeNext { error ->
                        setAccountSyncError(accountId, error.message ?: "Sync failed")
                    }
            }
    }

    override fun syncAccountFull(accountId: String): Completable {
        return Completable.fromAction {
            accountDao.updateAccount(accountId, "SYNCING")
        }.subscribeOn(Schedulers.io())
            .flatMapCompletable {
                accountsApi.syncAccount(accountId)
                    .flatMapCompletable { response ->
                        if (response.success && response.data != null) {
                            val data = response.data!!
                            return@flatMapCompletable updateAccountBalance(
                                accountId,
                                Money(data.balance, "INR"),
                                data.availableBalance?.let { Money(it, "INR") },
                                data.lastSyncedAt ?: java.time.Instant.now().toString()
                            )
                        } else {
                            return@flatMapCompletable setAccountSyncError(
                                accountId,
                                response.error?.message ?: "Sync failed"
                            )
                        }
                    }
                    .onErrorResumeNext { error ->
                        setAccountSyncError(accountId, error.message ?: "Sync failed")
                    }
            }
    }

    override fun deleteAccount(accountId: String): Completable {
        return Completable.fromAction {
            accountDao.deleteAccount(accountId)
        }.subscribeOn(Schedulers.io())
    }

    override fun getPendingSyncAccounts(userId: String): Flowable<List<Account>> {
        return accountDao.getPendingSyncAccounts(userId)
            .map { entities -> entities.map { AccountMapper.toDomain(it) } }
    }

    private fun toEntity(account: Account): AccountEntity {
        return AccountEntity(
            id = account.id,
            userId = account.userId,
            institutionId = account.institution.id,
            institutionName = account.institution.name,
            institutionLogo = account.institution.logo,
            institutionType = account.institution.type.name,
            accountType = account.accountType.name,
            accountSubType = account.accountSubType,
            maskedNumber = account.maskedNumber,
            currency = account.currency,
            balance = account.balance.amount,
            availableBalance = account.availableBalance?.amount,
            creditLimit = account.creditLimit?.amount,
            isActive = account.isActive,
            lastSyncedAt = account.lastSyncedAt,
            lastSuccessfulSync = account.lastSuccessfulSync,
            syncStatus = account.syncStatus.name,
            syncError = account.syncError,
            consentId = account.consentId,
            createdAt = account.createdAt,
            updatedAt = account.updatedAt
        )
    }
}