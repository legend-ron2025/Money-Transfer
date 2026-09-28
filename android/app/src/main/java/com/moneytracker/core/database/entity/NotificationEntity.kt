package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["is_read"]),
        Index(value = ["created_at"]),
        Index(value = ["type"])
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
data class NotificationEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "data") val data: String?,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    @ColumnInfo(name = "priority") val priority: String,
    @ColumnInfo(name = "action_url") val actionUrl: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "read_at") val readAt: String?
)