package com.example.managementapp.util.apartment

import com.example.managementapp.main.model.apartment.Apartment

data class ApartmentsState(
    val apartments: List<Apartment>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)