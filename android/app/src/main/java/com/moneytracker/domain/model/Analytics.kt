package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsOverview(
    val totalBalance: Money,
    val totalIncome: Money,
    val totalExpense: Money,
    val netSavings: Money,
    val savingsRate: Percentage,
    val balanceChange: Money,
    val incomeChange: Money,
    val expenseChange: Money,
    val period: String,
    val currency: String,
    val accountBalances: List<AccountBalance>,
    val topCategories: List<CategorySpend>,
    val recentTransactions: List<Transaction>,
    val upcomingPayments: List<UpcomingPayment>,
    val budgetAlerts: List<BudgetAlert>
)

@Serializable
data class AccountBalance(
    val accountId: String,
    val accountName: String,
    val balance: Money,
    val accountType: Account.AccountType
)

@Serializable
data class CategorySpend(
    val categoryId: String,
    val categoryName: String,
    val icon: String,
    val amount: Money,
    val percentage: Percentage,
    val transactionCount: Int
)

@Serializable
data class UpcomingPayment(
    val id: String,
    val name: String,
    val amount: Money,
    val dueDate: String,
    val type: String,
    val merchantName: String?
)

@Serializable
data class BudgetAlert(
    val budgetId: String,
    val categoryName: String,
    val percentageUsed: Percentage,
    val status: BudgetStatus
)

@Serializable
data class SpendingAnalytics(
    val totalSpent: Money,
    val averageDaily: Money,
    val averageWeekly: Money,
    val averageMonthly: Money,
    val byCategory: List<CategorySpend>,
    val byMerchant: List<MerchantSpend>,
    val byPaymentChannel: List<ChannelSpend>,
    val dailyTrend: List<DailySpend>,
    val weeklyTrend: List<WeeklySpend>
)

@Serializable
data class IncomeAnalytics(
    val totalIncome: Money,
    val bySource: List<SourceIncome>,
    val monthlyTrend: List<MonthlyIncome>
)

@Serializable
data class CashFlowAnalytics(
    val openingBalance: Money,
    val closingBalance: Money,
    val netFlow: Money,
    val inflows: List<Flow>,
    val outflows: List<Flow>,
    val dailyBalance: List<DailyBalance>
)

@Serializable
data class TrendAnalytics(
    val monthlyIncome: List<MonthlyValue>,
    val monthlyExpense: List<MonthlyValue>,
    val monthlySavings: List<MonthlyValue>,
    val monthlyBalance: List<MonthlyValue>,
    val categoryTrends: List<CategoryTrend>
)

@Serializable
data class MerchantSpend(
    val merchantName: String,
    val amount: Money,
    val transactionCount: Int,
    val category: String
)

@Serializable
data class ChannelSpend(
    val channel: String,
    val amount: Money,
    val percentage: Percentage
)

@Serializable
data class DailySpend(
    val date: String,
    val amount: Money,
    val transactionCount: Int
)

@Serializable
data class WeeklySpend(
    val weekStart: String,
    val weekEnd: String,
    val amount: Money
)

@Serializable
data class SourceIncome(
    val source: String,
    val amount: Money,
    val percentage: Percentage
)

@Serializable
data class MonthlyIncome(
    val month: String,
    val amount: Money
)

@Serializable
data class Flow(
    val category: String,
    val amount: Money,
    val percentage: Percentage
)

@Serializable
data class DailyBalance(
    val date: String,
    val balance: Money
)

@Serializable
data class MonthlyValue(
    val month: String,
    val value: Money
)

@Serializable
data class CategoryTrend(
    val categoryId: String,
    val categoryName: String,
    val monthlyValues: List<MonthlyValue>
)

@Serializable
data class Insight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val value: Money?,
    val comparisonValue: Money?,
    val severity: InsightSeverity,
    val actionUrl: String?
)

enum class InsightType {
    SPENDING_INCREASE, SPENDING_DECREASE, BUDGET_WARNING, SUBSCRIPTION_UPCOMING, 
    GOAL_PROGRESS, UNUSUAL_TRANSACTION, INCOME_CHANGE, SAVINGS_RATE
}

enum class InsightSeverity {
    INFO, WARNING, CRITICAL
}