package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.CategoryDto
import com.moneytracker.core.network.dto.CreateCategoryRequest
import com.moneytracker.core.network.dto.UpdateCategoryRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoriesApi {
    @GET("/categories")
    fun getCategories(): Single<ApiResponse<List<CategoryDto>>>

    @GET("/categories/{id}")
    fun getCategory(@Path("id") id: String): Single<ApiResponse<CategoryDto>>

    @POST("/categories")
    fun createCategory(@Body request: CreateCategoryRequest): Single<ApiResponse<CategoryDto>>

    @PATCH("/categories/{id}")
    fun updateCategory(@Path("id") id: String, @Body request: UpdateCategoryRequest): Single<ApiResponse<CategoryDto>>

    @GET("/categories/tree")
    fun getCategoryTree(): Single<ApiResponse<List<CategoryTreeDto>>>
}

data class CategoryTreeDto(
    val id: String,
    val name: String,
    val icon: String,
    val type: String,
    val children: List<CategoryTreeDto> = emptyList()
)