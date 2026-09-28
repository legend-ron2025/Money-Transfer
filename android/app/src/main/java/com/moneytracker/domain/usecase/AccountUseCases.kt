package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.Account
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.AccountRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(userId: String): Flowable<List<Account>> {
        return accountRepository.getActiveAccounts(userId)
    }
}

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(accountId: String): Maybe<Account> {
        return accountRepository.getAccount(accountId)
    }
}

class GetTotalBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(userId: String): Single<Money> {
        return accountRepository.getTotalBalance(userId)
    }
}

class SyncAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(accountId: String): Completable {
        return accountRepository.syncAccount(accountId)
    }
}

class SyncAllAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(userId: String): Completable {
        return accountRepository.getActiveAccounts(userId)
            .flatMapCompletable { accounts ->
                Completable.fromAction {
                    accounts.forEach { account ->
                        accountRepository.syncAccount(account.id)
                    }
                }
            }
    }
}

class AddManualAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(account: Account): Completable {
        return accountRepository.insertAccount(account)
    }
}

class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(account: Account): Completable {
        return accountRepository.updateAccount(account)
    }
}

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(accountId: String): Completable {
        return accountRepository.deleteAccount(accountId)
    }
}

class ReconnectAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(accountId: String): Completable {
        return accountRepository.syncAccountFull(accountId)
    }
}