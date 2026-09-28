package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.model.BudgetWithCategory
import com.moneytracker.domain.repository.BudgetRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(userId: String): Flowable<List<Budget>> {
        return budgetRepository.getBudgets(userId)
    }
}

class GetActiveBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(userId: String): Flowable<List<Budget>> {
        return budgetRepository.getActiveBudgets(userId)
    }
}

class GetBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budgetId: String): Maybe<Budget> {
        return budgetRepository.getBudget(budgetId)
    }
}

class GetBudgetByCategoryUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(userId: String, categoryId: String): Maybe<Budget> {
        return budgetRepository.getBudgetByCategory(userId, categoryId)
    }
}

class GetBudgetsWithCategoryUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(userId: String): Flowable<List<BudgetWithCategory>> {
        return budgetRepository.getBudgetsWithCategory(userId)
    }
}

class CreateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budget: Budget): Completable {
        return budgetRepository.insertBudget(budget)
    }
}

class UpdateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budget: Budget): Completable {
        return budgetRepository.updateBudget(budget)
    }
}

class UpdateBudgetSpentUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budgetId: String, spentAmount: Money): Completable {
        return budgetRepository.updateSpentAmount(budgetId, spentAmount)
    }
}

class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budgetId: String): Completable {
        return budgetRepository.deleteBudget(budgetId)
    }
}