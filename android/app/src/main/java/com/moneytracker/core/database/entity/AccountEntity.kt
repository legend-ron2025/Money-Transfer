package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["institution_id"]),
        Index(value = ["consent_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class AccountEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "institution_id") val institutionId: String,
    @ColumnInfo(name = "institution_name") val institutionName: String,
    @ColumnInfo(name = "institution_logo") val institutionLogo: String?,
    @ColumnInfo(name = "institution_type") val institutionType: String,
    @ColumnInfo(name = "account_type") val accountType: String,
    @ColumnInfo(name = "account_sub_type") val accountSubType: String?,
    @ColumnInfo(name = "masked_number") val maskedNumber: String,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "balance") val balance: Double,
    @ColumnInfo(name = "available_balance") val availableBalance: Double?,
    @ColumnInfo(name = "credit_limit") val creditLimit: Double?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "last_synced_at") val lastSyncedAt: String?,
    @ColumnInfo(name = "last_successful_sync") val lastSuccessfulSync: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: String,
    @ColumnInfo(name = "sync_error") val syncError: String?,
    @ColumnInfo(name = "consent_id") val consentId: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)