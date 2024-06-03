package com.example.managementapp.util

data class AuthState(
    val isLoading: Boolean = false,
    val registerUsername : String = "",
    val registerPassword: String = "",
    val registerEmail: String = "",
    val loginUsername: String = "",
    val loginPassword: String = ""
)
