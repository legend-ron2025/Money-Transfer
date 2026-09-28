package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsOverviewDto(
    @SerializedName("totalBalance") val totalBalance: Double,
    @SerializedName("totalIncome") val totalIncome: Double,
    @SerializedName("totalExpense") val totalExpense: Double,
    @SerializedName("netSavings") val netSavings: Double,
    @SerializedName("savingsRate") val savingsRate: Double,
    @SerializedName("balanceChange") val balanceChange: Double,
    @SerializedName("incomeChange") val incomeChange: Double,
    @SerializedName("expenseChange") val expenseChange: Double,
    @SerializedName("period") val period: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("accountBalances") val accountBalances: List<AccountBalanceDto>,
    @SerializedName("topCategories") val topCategories: List<CategorySpendDto>,
    @SerializedName("recentTransactions") val recentTransactions: List<TransactionDto>,
    @SerializedName("upcomingPayments") val upcomingPayments: List<UpcomingPaymentDto>,
    @SerializedName("budgetAlerts") val budgetAlerts: List<BudgetAlertDto>
)

@Serializable
data class AccountBalanceDto(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("accountName") val accountName: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("accountType") val accountType: String
)

@Serializable
data class CategorySpendDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double,
    @SerializedName("transactionCount") val transactionCount: Int
)

@Serializable
data class UpcomingPaymentDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("type") val type: String,
    @SerializedName("merchantName") val merchantName: String?
)

@Serializable
data class BudgetAlertDto(
    @SerializedName("budgetId") val budgetId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("percentageUsed") val percentageUsed: Double,
    @SerializedName("status") val status: String
)