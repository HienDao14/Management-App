package com.example.managementapp.auth.data.remote.model

import com.example.managementapp.auth.data.local.User
import com.squareup.moshi.Json

data class AuthResponse(
    @field:Json(name = "success")
    val success: Boolean,
    @field:Json(name = "user")
    val user: User
)
