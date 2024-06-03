package com.example.managementapp.auth.data.remote.api

import com.example.managementapp.auth.data.remote.model.AuthResponse
import com.example.managementapp.auth.data.remote.model.Response
import com.example.managementapp.auth.domain.model.AuthRequest
import com.example.managementapp.auth.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: AuthRequest
    )

    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): LoginResponse

    @POST("auth/authenticate")
    suspend fun authenticate(
    ): AuthResponse

    @POST("auth/forgot-password")
    suspend fun getOTP(
        @Query("userEmail") email: String
    ): Response

    @POST("auth/verifyOTP")
    suspend fun verifyOTP(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response

    @POST("auth/updatePass")
    suspend fun updatePassword(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response
}
