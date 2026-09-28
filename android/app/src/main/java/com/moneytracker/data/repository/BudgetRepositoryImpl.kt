package com.moneytracker.data.repository

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.BudgetDao
import com.moneytracker.core.database.entity.BudgetEntity
import com.moneytracker.data.mapper.BudgetMapper
import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.BudgetRepository
import com.moneytracker.domain.repository.BudgetWithCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val budgetDao: BudgetDao,
    private val budgetsApi: com.moneytracker.core.network.api.BudgetsApi
) : BudgetRepository {

    override fun getBudgets(userId: String): Flowable<List<Budget>> {
        return budgetDao.getAllBudgets(userId)
            .map { entities -> entities.map { BudgetMapper.toDomain(it) } }
    }

    override fun getActiveBudgets(userId: String): Flowable<List<Budget>> {
        return budgetDao.getActiveBudgets(userId)
            .map { entities -> entities.map { BudgetMapper.toDomain(it) } }
    }

    override fun getBudget(budgetId: String): Maybe<Budget> {
        return budgetDao.getBudget(budgetId)
            .map { BudgetMapper.toDomain(it) }
    }

    override fun getBudgetByCategory(userId: String, categoryId: String): Maybe<Budget> {
        return budgetDao.getBudgetByCategory(userId, categoryId)
            .map { BudgetMapper.toDomain(it) }
    }

    override fun getBudgetsByPeriod(userId: String, period: Budget.BudgetPeriod, startDate: String): Flowable<List<Budget>> {
        return budgetDao.getBudgetsByPeriod(userId, period.name, startDate)
            .map { entities -> entities.map { BudgetMapper.toDomain(it) } }
    }

    override fun getBudgetsWithCategory(userId: String): Flowable<List<BudgetWithCategory>> {
        return budgetDao.getBudgetsWithCategory(userId)
            .map { list ->
                list.map { dto ->
                    BudgetWithCategory(
                        budget = Budget(
                            id = dto.id,
                            userId = dto.user_id,
                            category = com.moneytracker.domain.model.Category(
                                id = dto.category_id,
                                name = dto.category_name,
                                parentId = null,
                                icon = dto.category_icon,
                                type = com.moneytracker.domain.model.Category.CategoryType.EXPENSE,
                                color = null,
                                isSystem = true,
                                sortOrder = 0
                            ),
                            amount = Money(dto.amount, "INR"),
                            period = com.moneytracker.domain.model.BudgetPeriod.valueOf(dto.period),
                            startDate = dto.start_date,
                            endDate = dto.end_date,
                            spentAmount = Money(dto.spent_amount, "INR"),
                            alertThreshold = dto.alert_threshold,
                            isActive = dto.is_active,
                            createdAt = dto.created_at,
                            updatedAt = dto.updated_at
                        ),
                        category = com.moneytracker.domain.model.Category(
                            id = dto.category_id,
                            name = dto.category_name,
                            parentId = null,
                            icon = dto.category_icon,
                            type = com.moneytracker.domain.model.Category.CategoryType.EXPENSE,
                            color = null,
                            isSystem = true,
                            sortOrder = 0
                        )
                    )
                }
            }
    }

    override fun insertBudget(budget: Budget): Completable {
        return Completable.fromAction {
            val entity = toEntity(budget)
            budgetDao.insertBudget(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateBudget(budget: Budget): Completable {
        return Completable.fromAction {
            val entity = toEntity(budget)
            budgetDao.updateBudget(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateSpentAmount(budgetId: String, spentAmount: Money): Completable {
        return Completable.fromAction {
            budgetDao.updateSpentAmount(budgetId, spentAmount.amount)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteBudget(budgetId: String): Completable {
        return Completable.fromAction {
            budgetDao.deleteBudgetById(budgetId)
        }.subscribeOn(Schedulers.io())
    }

    private fun toEntity(budget: Budget): BudgetEntity {
        return BudgetEntity(
            id = budget.id,
            userId = budget.userId,
            categoryId = budget.category.id,
            amount = budget.amount.amount,
            period = budget.period.name,
            startDate = budget.startDate,
            endDate = budget.endDate,
            spentAmount = budget.spentAmount.amount,
            alertThreshold = budget.alertThreshold,
            isActive = budget.isActive,
            createdAt = budget.createdAt,
            updatedAt = budget.updatedAt
        )
    }
}