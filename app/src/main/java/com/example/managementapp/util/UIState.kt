package com.example.managementapp.util


sealed class UiState<out T> {
    data object Empty : UiState<Nothing>()

    data object Loading : UiState<Nothing>()

    data class Success<out T>(val data: T) : UiState<T>()

    data class Error(val data: String?) : UiState<Nothing>()
}