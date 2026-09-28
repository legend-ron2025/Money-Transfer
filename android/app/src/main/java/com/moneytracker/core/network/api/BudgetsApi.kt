package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.BudgetDto
import com.moneytracker.core.network.dto.CreateBudgetRequest
import com.moneytracker.core.network.dto.UpdateBudgetRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BudgetsApi {
    @GET("/budgets")
    fun getBudgets(): Single<ApiResponse<List<BudgetDto>>>

    @GET("/budgets/{id}")
    fun getBudget(@Path("id") id: String): Single<ApiResponse<BudgetDto>>

    @POST("/budgets")
    fun createBudget(@Body request: CreateBudgetRequest): Single<ApiResponse<BudgetDto>>

    @PATCH("/budgets/{id}")
    fun updateBudget(@Path("id") id: String, @Body request: UpdateBudgetRequest): Single<ApiResponse<BudgetDto>>

    @DELETE("/budgets/{id}")
    fun deleteBudget(@Path("id") id: String): Single<ApiResponse<Unit>>

    @GET("/budgets/status")
    fun getBudgetStatus(): Single<ApiResponse<List<BudgetStatusDto>>>
}

data class BudgetStatusDto(
    val budgetId: String,
    val categoryId: String,
    val categoryName: String,
    val budgetAmount: Double,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val status: String,
    val periodStart: String,
    val periodEnd: String
)