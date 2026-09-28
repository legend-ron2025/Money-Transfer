package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.AuthRequest
import com.moneytracker.core.network.dto.AuthResponse
import com.moneytracker.core.network.dto.RefreshTokenRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    fun login(@Body request: AuthRequest): Single<ApiResponse<AuthResponse>>

    @POST("/auth/register")
    fun register(@Body request: AuthRequest): Single<ApiResponse<AuthResponse>>

    @POST("/auth/refresh")
    fun refreshToken(@Body request: RefreshTokenRequest): Single<ApiResponse<AuthResponse>>

    @POST("/auth/logout")
    fun logout(): Single<ApiResponse<Unit>>

    @POST("/auth/forgot-password")
    fun forgotPassword(@Body email: String): Single<ApiResponse<Unit>>

    @POST("/auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Single<ApiResponse<Unit>>
}

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)