package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.NotificationDto
import com.moneytracker.core.network.dto.NotificationPreferencesDto
import com.moneytracker.core.network.dto.UpdateNotificationPreferencesRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationsApi {
    @GET("/notifications")
    fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): Single<ApiResponse<PaginatedResponse<NotificationDto>>>

    @GET("/notifications/preferences")
    fun getPreferences(): Single<ApiResponse<NotificationPreferencesDto>>

    @PATCH("/notifications/preferences")
    fun updatePreferences(@Body request: UpdateNotificationPreferencesRequest): Single<ApiResponse<NotificationPreferencesDto>>

    @POST("/notifications/{id}/read")
    fun markAsRead(@Path("id") id: String): Single<ApiResponse<Unit>>

    @POST("/notifications/read-all")
    fun markAllAsRead(): Single<ApiResponse<Unit>>

    @DELETE("/notifications/{id}")
    fun deleteNotification(@Path("id") id: String): Single<ApiResponse<Unit>>
}

import retrofit2.http.Query