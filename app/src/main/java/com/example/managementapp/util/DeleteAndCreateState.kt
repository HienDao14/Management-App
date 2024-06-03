package com.example.managementapp.util

data class DeleteAndCreateState(
    var success: Boolean ?= null,
    var isLoading: Boolean = false,
    var error: String ?= null
)
