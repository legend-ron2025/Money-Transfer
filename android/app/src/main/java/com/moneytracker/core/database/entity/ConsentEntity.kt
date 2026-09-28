package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "consents",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["provider_id"]),
        Index(value = ["consent_id"], unique = true),
        Index(value = ["status"])
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
data class ConsentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "provider_id") val providerId: String,
    @ColumnInfo(name = "provider_name") val providerName: String,
    @ColumnInfo(name = "provider_logo") val providerLogo: String?,
    @ColumnInfo(name = "consent_id") val consentId: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "granted_at") val grantedAt: String,
    @ColumnInfo(name = "expires_at") val expiresAt: String?,
    @ColumnInfo(name = "revoked_at") val revokedAt: String?,
    @ColumnInfo(name = "accounts") val accounts: String,
    @ColumnInfo(name = "permissions") val permissions: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)