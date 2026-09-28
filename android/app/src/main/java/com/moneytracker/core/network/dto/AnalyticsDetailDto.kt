package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SpendingAnalyticsDto(
    @SerializedName("totalSpent") val totalSpent: Double,
    @SerializedName("averageDaily") val averageDaily: Double,
    @SerializedName("averageWeekly") val averageWeekly: Double,
    @SerializedName("averageMonthly") val averageMonthly: Double,
    @SerializedName("byCategory") val byCategory: List<CategorySpendDto>,
    @SerializedName("byMerchant") val byMerchant: List<MerchantSpendDto>,
    @SerializedName("byPaymentChannel") val byPaymentChannel: List<ChannelSpendDto>,
    @SerializedName("dailyTrend") val dailyTrend: List<DailySpendDto>,
    @SerializedName("weeklyTrend") val weeklyTrend: List<WeeklySpendDto>
)

@Serializable
data class IncomeAnalyticsDto(
    @SerializedName("totalIncome") val totalIncome: Double,
    @SerializedName("bySource") val bySource: List<SourceIncomeDto>,
    @SerializedName("monthlyTrend") val monthlyTrend: List<MonthlyIncomeDto>
)

@Serializable
data class CashFlowAnalyticsDto(
    @SerializedName("openingBalance") val openingBalance: Double,
    @SerializedName("closingBalance") val closingBalance: Double,
    @SerializedName("netFlow") val netFlow: Double,
    @SerializedName("inflows") val inflows: List<FlowDto>,
    @SerializedName("outflows") val outflows: List<FlowDto>,
    @SerializedName("dailyBalance") val dailyBalance: List<DailyBalanceDto>
)

@Serializable
data class CategoryAnalyticsDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double,
    @SerializedName("transactionCount") val transactionCount: Int,
    @SerializedName("averageAmount") val averageAmount: Double,
    @SerializedName("trend") val trend: String
)

@Serializable
data class TrendAnalyticsDto(
    @SerializedName("monthlyIncome") val monthlyIncome: List<MonthlyValueDto>,
    @SerializedName("monthlyExpense") val monthlyExpense: List<MonthlyValueDto>,
    @SerializedName("monthlySavings") val monthlySavings: List<MonthlyValueDto>,
    @SerializedName("monthlyBalance") val monthlyBalance: List<MonthlyValueDto>,
    @SerializedName("categoryTrends") val categoryTrends: List<CategoryTrendDto>
)

@Serializable
data class MerchantSpendDto(
    @SerializedName("merchantName") val merchantName: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("transactionCount") val transactionCount: Int,
    @SerializedName("category") val category: String
)

@Serializable
data class ChannelSpendDto(
    @SerializedName("channel") val channel: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double
)

@Serializable
data class DailySpendDto(
    @SerializedName("date") val date: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("transactionCount") val transactionCount: Int
)

@Serializable
data class WeeklySpendDto(
    @SerializedName("weekStart") val weekStart: String,
    @SerializedName("weekEnd") val weekEnd: String,
    @SerializedName("amount") val amount: Double
)

@Serializable
data class SourceIncomeDto(
    @SerializedName("source") val source: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double
)

@Serializable
data class MonthlyIncomeDto(
    @SerializedName("month") val month: String,
    @SerializedName("amount") val amount: Double
)

@Serializable
data class FlowDto(
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double
)

@Serializable
data class DailyBalanceDto(
    @SerializedName("date") val date: String,
    @SerializedName("balance") val balance: Double
)

@Serializable
data class MonthlyValueDto(
    @SerializedName("month") val month: String,
    @SerializedName("value") val value: Double
)

@Serializable
data class CategoryTrendDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("monthlyValues") val monthlyValues: List<MonthlyValueDto>
)