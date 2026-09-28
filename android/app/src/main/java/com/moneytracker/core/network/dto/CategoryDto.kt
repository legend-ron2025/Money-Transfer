package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("icon") val icon: String,
    @SerializedName("type") val type: String,
    @SerializedName("color") val color: String?,
    @SerializedName("isSystem") val isSystem: Boolean,
    @SerializedName("sortOrder") val sortOrder: Int,
    @SerializedName("children") val children: List<CategoryDto> = emptyList()
)

@Serializable
data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("icon") val icon: String,
    @SerializedName("type") val type: String,
    @SerializedName("color") val color: String?
)

@Serializable
data class UpdateCategoryRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("sortOrder") val sortOrder: Int? = null
)