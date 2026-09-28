package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val parentId: String?,
    val icon: String,
    val type: CategoryType,
    val color: String?,
    val isSystem: Boolean,
    val sortOrder: Int,
    val children: List<Category> = emptyList()
) {
    val fullName: String
        get() = if (parentId != null) {
            // Parent name would need to be resolved from the hierarchy
            name
        } else {
            name
        }

    val isParent: Boolean
        get() = children.isNotEmpty()

    val isLeaf: Boolean
        get() = children.isEmpty()

    enum class CategoryType {
        INCOME, EXPENSE, TRANSFER
    }

    companion object {
        fun income(name: String, icon: String, id: String = java.util.UUID.randomUUID().toString()): Category {
            return Category(id, name, null, icon, CategoryType.INCOME, null, true, 0)
        }

        fun expense(name: String, icon: String, id: String = java.util.UUID.randomUUID().toString()): Category {
            return Category(id, name, null, icon, CategoryType.EXPENSE, null, true, 0)
        }
    }
}