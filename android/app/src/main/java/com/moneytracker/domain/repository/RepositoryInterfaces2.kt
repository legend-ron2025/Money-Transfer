package com.moneytracker.domain.repository

import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.model.Category
import com.moneytracker.domain.model.Goal
import com.moneytracker.domain.model.Notification
import com.moneytracker.domain.model.AnalyticsOverview
import com.moneytracker.domain.model.SpendingAnalytics
import com.moneytracker.domain.model.IncomeAnalytics
import com.moneytracker.domain.model.CashFlowAnalytics
import com.moneytracker.domain.model.TrendAnalytics
import com.moneytracker.domain.model.Insight
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface CategoryRepository {
    fun getCategoriesByType(type: Category.CategoryType): Flowable<List<Category>>
    fun getParentCategoriesByType(type: Category.CategoryType): Flowable<List<Category>>
    fun getChildCategories(parentId: String): Flowable<List<Category>>
    fun getAllCategories(): Flowable<List<Category>>
    fun getCategory(categoryId: String): Maybe<Category>
    fun insertCategory(category: Category): Completable
    fun insertCategories(categories: List<Category>): Completable
    fun updateCategory(category: Category): Completable
    fun deleteCustomCategory(categoryId: String): Completable
}

interface BudgetRepository {
    fun getBudgets(userId: String): Flowable<List<Budget>>
    fun getActiveBudgets(userId: String): Flowable<List<Budget>>
    fun getBudget(budgetId: String): Maybe<Budget>
    fun getBudgetByCategory(userId: String, categoryId: String): Maybe<Budget>
    fun getBudgetsByPeriod(userId: String, period: Budget.BudgetPeriod, startDate: String): Flowable<List<Budget>>
    fun getBudgetsWithCategory(userId: String): Flowable<List<BudgetWithCategory>>
    fun insertBudget(budget: Budget): Completable
    fun updateBudget(budget: Budget): Completable
    fun updateSpentAmount(budgetId: String, spentAmount: Money): Completable
    fun deleteBudget(budgetId: String): Completable
}

data class BudgetWithCategory(
    val budget: Budget,
    val category: Category
)

interface GoalRepository {
    fun getGoals(userId: String): Flowable<List<Goal>>
    fun getActiveGoals(userId: String): Flowable<List<Goal>>
    fun getGoalsByStatus(userId: String, status: Goal.GoalStatus): Flowable<List<Goal>>
    fun getGoal(goalId: String): Maybe<Goal>
    fun insertGoal(goal: Goal): Completable
    fun updateGoal(goal: Goal): Completable
    fun updateCurrentAmount(goalId: String, currentAmount: Money): Completable
    fun updateStatus(goalId: String, status: Goal.GoalStatus): Completable
    fun deleteGoal(goalId: String): Completable
}

interface NotificationRepository {
    fun getNotifications(userId: String, page: Int, limit: Int): Flowable<PaginatedResult<Notification>>
    fun getUnreadNotifications(userId: String, limit: Int): Flowable<List<Notification>>
    fun getUnreadCount(userId: String): Single<Int>
    fun insertNotification(notification: Notification): Completable
    fun insertNotifications(notifications: List<Notification>): Completable
    fun markAsRead(notificationId: String): Completable
    fun markAllAsRead(userId: String): Completable
    fun deleteNotification(notificationId: String): Completable
    fun deleteOldNotifications(userId: String, cutoffDate: String): Completable
}

interface AnalyticsRepository {
    fun getOverview(userId: String, period: String): Single<AnalyticsOverview>
    fun getSpendingAnalytics(userId: String, period: String): Single<SpendingAnalytics>
    fun getIncomeAnalytics(userId: String, period: String): Single<IncomeAnalytics>
    fun getCashFlowAnalytics(userId: String, period: String): Single<CashFlowAnalytics>
    fun getTrendAnalytics(userId: String, period: String, months: Int): Single<TrendAnalytics>
    fun getInsights(userId: String): Single<List<Insight>>
    fun getCategoryBreakdown(userId: String, period: String, type: Category.CategoryType): Single<List<CategorySpend>>
    fun invalidateCache(userId: String): Completable
}