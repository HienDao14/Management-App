package com.example.managementapp.main.remote.api

import com.example.managementapp.auth.data.local.User
import com.hadiyarajesh.flower_core.ApiResponse
import retrofit2.http.GET

interface UserApi {

    @GET("user/get-user")
    suspend fun getUser(): User
}