package com.example.managementapp.auth.domain.model

data class AuthRequest(
    val username: String,
    val password: String,
    val email: String? = null
)
