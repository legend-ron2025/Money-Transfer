package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("deviceId") val deviceId: String?,
    @SerializedName("deviceName") val deviceName: String?,
    @SerializedName("platform") val platform: String = "android",
    @SerializedName("fcmToken") val fcmToken: String?
)

@Serializable
data class AuthResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("tokenType") val tokenType: String = "Bearer"
)

@Serializable
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

@Serializable
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("isEmailVerified") val isEmailVerified: Boolean,
    @SerializedName("isPhoneVerified") val isPhoneVerified: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("preferences") val preferences: UserPreferencesDto?
)

@Serializable
data class UserPreferencesDto(
    @SerializedName("currency") val currency: String = "INR",
    @SerializedName("language") val language: String = "en",
    @SerializedName("theme") val theme: String = "system",
    @SerializedName("notificationsEnabled") val notificationsEnabled: Boolean = true,
    @SerializedName("biometricEnabled") val biometricEnabled: Boolean = false,
    @SerializedName("autoSyncEnabled") val autoSyncEnabled: Boolean = true,
    @SerializedName("syncFrequency") val syncFrequency: String = "15min"
)