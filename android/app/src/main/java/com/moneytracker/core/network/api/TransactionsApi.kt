package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.TransactionDto
import com.moneytracker.core.network.dto.TransactionFilter
import com.moneytracker.core.network.dto.TransactionUpdateRequest
import com.moneytracker.core.network.dto.PaginatedResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionsApi {
    @GET("/transactions")
    fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("account") account: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("minAmount") minAmount: Double? = null,
        @Query("maxAmount") maxAmount: Double? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String = "date",
        @Query("order") order: String = "desc"
    ): Single<ApiResponse<PaginatedResponse<TransactionDto>>>

    @GET("/transactions/{id}")
    fun getTransaction(@Path("id") id: String): Single<ApiResponse<TransactionDto>>

    @PATCH("/transactions/{id}")
    fun updateTransaction(
        @Path("id") id: String,
        @Body request: TransactionUpdateRequest
    ): Single<ApiResponse<TransactionDto>>

    @DELETE("/transactions/{id}")
    fun deleteTransaction(@Path("id") id: String): Single<ApiResponse<Unit>>

    @POST("/transactions/bulk-update")
    fun bulkUpdateTransactions(
        @Body request: BulkUpdateRequest
    ): Single<ApiResponse<List<TransactionDto>>>
}

data class BulkUpdateRequest(
    val ids: List<String>,
    val categoryId: String? = null,
    val notes: String? = null,
    val isTransfer: Boolean? = null
)