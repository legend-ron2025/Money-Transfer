package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.UserDto
import com.moneytracker.core.network.dto.UserPreferencesDto
import com.moneytracker.domain.model.User
import com.moneytracker.domain.model.UserPreferences

object UserMapper {

    fun toDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            email = dto.email,
            phone = dto.phone,
            name = dto.name,
            avatar = dto.avatar,
            isEmailVerified = dto.isEmailVerified,
            isPhoneVerified = dto.isPhoneVerified,
            preferences = toDomain(dto.preferences),
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDomain(dto: UserPreferencesDto?): UserPreferences {
        return dto?.let {
            UserPreferences(
                currency = it.currency,
                language = it.language,
                theme = when (it.theme) {
                    "light" -> UserPreferences.Theme.LIGHT
                    "dark" -> UserPreferences.Theme.DARK
                    else -> UserPreferences.Theme.SYSTEM
                },
                notificationsEnabled = it.notificationsEnabled,
                biometricEnabled = it.biometricEnabled,
                autoSyncEnabled = it.autoSyncEnabled,
                syncFrequency = it.syncFrequency
            )
        } ?: UserPreferences()
    }

    fun toDto(domain: UserPreferences): UserPreferencesDto {
        return UserPreferencesDto(
            currency = domain.currency,
            language = domain.language,
            theme = when (domain.theme) {
                UserPreferences.Theme.LIGHT -> "light"
                UserPreferences.Theme.DARK -> "dark"
                UserPreferences.Theme.SYSTEM -> "system"
            },
            notificationsEnabled = domain.notificationsEnabled,
            biometricEnabled = domain.biometricEnabled,
            autoSyncEnabled = domain.autoSyncEnabled,
            syncFrequency = domain.syncFrequency
        )
    }
}