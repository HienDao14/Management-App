package com.example.managementapp.auth.data.remote.model

import com.squareup.moshi.Json

data class UserDto(
    @field:Json(name = "_id")
    val userId: String,
    @field:Json(name = "username")
    val username: String,
    @field:Json(name = "email")
    val email: String,
)
