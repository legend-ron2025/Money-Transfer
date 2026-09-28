package com.moneytracker.core.network.api

import com.moneytracker.core.network.dto.ApiResponse
import com.moneytracker.core.network.dto.GoalDto
import com.moneytracker.core.network.dto.CreateGoalRequest
import com.moneytracker.core.network.dto.UpdateGoalRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GoalsApi {
    @GET("/goals")
    fun getGoals(): Single<ApiResponse<List<GoalDto>>>

    @GET("/goals/{id}")
    fun getGoal(@Path("id") id: String): Single<ApiResponse<GoalDto>>

    @POST("/goals")
    fun createGoal(@Body request: CreateGoalRequest): Single<ApiResponse<GoalDto>>

    @PATCH("/goals/{id}")
    fun updateGoal(@Path("id") id: String, @Body request: UpdateGoalRequest): Single<ApiResponse<GoalDto>>

    @DELETE("/goals/{id}")
    fun deleteGoal(@Path("id") id: String): Single<ApiResponse<Unit>>

    @POST("/goals/{id}/contribute")
    fun contributeToGoal(@Path("id") id: String, @Body amount: Double): Single<ApiResponse<GoalDto>>
}