package com.example.managementapp.auth.domain.model

import com.example.managementapp.auth.data.local.User
import com.example.managementapp.auth.data.remote.model.UserDto
import com.squareup.moshi.Json

data class LoginResponse(
    @field:Json(name = "accessToken")
    val accessToken: String? = null,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "refreshToken")
    val refreshToken: String,
    @field:Json(name = "user")
    val user: User
)
