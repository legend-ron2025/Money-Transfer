package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.AccountDto
import com.moneytracker.core.network.dto.AccountSyncRequest
import com.moneytracker.core.network.dto.AccountSyncResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountsApi {
    @GET("/accounts")
    fun getAccounts(): Single<ApiResponse<List<AccountDto>>>

    @GET("/accounts/{id}")
    fun getAccount(@Path("id") id: String): Single<ApiResponse<AccountDto>>

    @POST("/accounts/sync")
    fun syncAccounts(@Body request: AccountSyncRequest): Single<ApiResponse<AccountSyncResponse>>

    @POST("/accounts/{id}/sync")
    fun syncAccount(@Path("id") id: String): Single<ApiResponse<AccountSyncResponse>>

    @POST("/accounts/{id}/reconnect")
    fun reconnectAccount(@Path("id") id: String): Single<ApiResponse<AccountDto>>

    @POST("/accounts/{id}/disconnect")
    fun disconnectAccount(@Path("id") id: String): Single<ApiResponse<Unit>>
}