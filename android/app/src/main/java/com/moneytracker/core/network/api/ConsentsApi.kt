package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.ConsentDto
import com.moneytracker.core.network.dto.CreateConsentRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConsentsApi {
    @GET("/consents")
    fun getConsents(): Single<ApiResponse<List<ConsentDto>>>

    @GET("/consents/{id}")
    fun getConsent(@Path("id") id: String): Single<ApiResponse<ConsentDto>>

    @POST("/consents")
    fun createConsent(@Body request: CreateConsentRequest): Single<ApiResponse<ConsentDto>>

    @DELETE("/consents/{id}")
    fun revokeConsent(@Path("id") id: String): Single<ApiResponse<Unit>>

    @POST("/consents/{id}/refresh")
    fun refreshConsent(@Path("id") id: String): Single<ApiResponse<ConsentDto>>

    @GET("/consents/providers")
    fun getAvailableProviders(): Single<ApiResponse<List<ProviderDto>>>
}

data class ProviderDto(
    val id: String,
    val name: String,
    val logo: String,
    val type: String,
    val supportedFeatures: List<String>
)