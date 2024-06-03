package com.example.managementapp.util

sealed class AuthUiEvent {
    data class RegisterUsernameChanged(val value: String): AuthUiEvent()
    data class RegisterPasswordChanged(val value: String): AuthUiEvent()
    data class RegisterEmailChanged(val value: String): AuthUiEvent()
    object Register: AuthUiEvent()

    data class LoginUsernameChanged(val value: String): AuthUiEvent()
    data class LoginPasswordChanged(val value: String): AuthUiEvent()
    object Login: AuthUiEvent()

}