package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["parent_id"]),
        Index(value = ["type"]),
        Index(value = ["sort_order"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class CategoryEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "parent_id") val parentId: String?,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "color") val color: String?,
    @ColumnInfo(name = "is_system") val isSystem: Boolean,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)