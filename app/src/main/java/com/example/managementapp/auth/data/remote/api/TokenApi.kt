package com.example.managementapp.auth.data.remote.api

import com.example.managementapp.auth.data.remote.model.Response
import com.example.managementapp.auth.data.remote.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface TokenApi {

    @POST("auth/refresh-token")
     fun refreshAccessToken(
        @Body refreshToken: String,
        @HeaderMap header: Map<String, String>
    ): Call<TokenResponse>
}