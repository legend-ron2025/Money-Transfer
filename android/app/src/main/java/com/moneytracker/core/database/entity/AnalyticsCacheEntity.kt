package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "analytics_cache",
    indices = [
        Index(value = ["user_id", "cache_key"], unique = true),
        Index(value = ["expires_at"])
    ]
)
@Serializable
data class AnalyticsCacheEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "cache_key") val cacheKey: String,
    @ColumnInfo(name = "data") val data: String,
    @ColumnInfo(name = "period") val period: String,
    @ColumnInfo(name = "expires_at") val expiresAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)