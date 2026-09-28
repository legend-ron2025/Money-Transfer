package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val phone: String?,
    val name: String?,
    val avatar: String?,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val preferences: UserPreferences,
    val createdAt: String,
    val updatedAt: String
) {
    val displayName: String
        get() = name ?: email.takeBefore("@") ?: "User"
}

@Serializable
data class UserPreferences(
    val currency: String = Constants.DEFAULT_CURRENCY,
    val language: String = "en",
    val theme: Theme = Theme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val autoSyncEnabled: Boolean = true,
    val syncFrequency: String = Constants.SyncFrequency.FIFTEEN_MIN
) {
    enum class Theme {
        LIGHT, DARK, SYSTEM
    }
}