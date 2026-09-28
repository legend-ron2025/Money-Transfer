package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
@Serializable
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "avatar") val avatar: String?,
    @ColumnInfo(name = "is_email_verified") val isEmailVerified: Boolean,
    @ColumnInfo(name = "is_phone_verified") val isPhoneVerified: Boolean,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "theme") val theme: String,
    @ColumnInfo(name = "notifications_enabled") val notificationsEnabled: Boolean,
    @ColumnInfo(name = "biometric_enabled") val biometricEnabled: Boolean,
    @ColumnInfo(name = "auto_sync_enabled") val autoSyncEnabled: Boolean,
    @ColumnInfo(name = "sync_frequency") val syncFrequency: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)