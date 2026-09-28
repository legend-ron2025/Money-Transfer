package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.AnalyticsOverviewDto
import com.moneytracker.core.network.dto.SpendingAnalyticsDto
import com.moneytracker.core.network.dto.IncomeAnalyticsDto
import com.moneytracker.core.network.dto.CashFlowAnalyticsDto
import com.moneytracker.core.network.dto.CategoryAnalyticsDto
import com.moneytracker.core.network.dto.TrendAnalyticsDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AnalyticsApi {
    @GET("/analytics/overview")
    fun getOverview(
        @Query("period") period: String = "month"
    ): Single<ApiResponse<AnalyticsOverviewDto>>

    @GET("/analytics/spending")
    fun getSpending(
        @Query("period") period: String = "month",
        @Query("groupBy") groupBy: String = "category"
    ): Single<ApiResponse<SpendingAnalyticsDto>>

    @GET("/analytics/income")
    fun getIncome(
        @Query("period") period: String = "month"
    ): Single<ApiResponse<IncomeAnalyticsDto>>

    @GET("/analytics/cashflow")
    fun getCashFlow(
        @Query("period") period: String = "month"
    ): Single<ApiResponse<CashFlowAnalyticsDto>>

    @GET("/analytics/categories")
    fun getCategoryBreakdown(
        @Query("period") period: String = "month",
        @Query("type") type: String = "expense"
    ): Single<ApiResponse<List<CategoryAnalyticsDto>>>

    @GET("/analytics/trends")
    fun getTrends(
        @Query("period") period: String = "month",
        @Query("months") months: Int = 6
    ): Single<ApiResponse<TrendAnalyticsDto>>

    @GET("/analytics/insights")
    fun getInsights(): Single<ApiResponse<List<InsightDto>>>
}

data class InsightDto(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val value: Double?,
    val comparisonValue: Double?,
    val severity: String,
    val actionUrl: String?
)