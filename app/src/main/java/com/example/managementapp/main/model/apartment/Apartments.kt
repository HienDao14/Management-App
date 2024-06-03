package com.example.managementapp.main.model.apartment

import com.example.managementapp.main.model.apartment.Apartment
import com.squareup.moshi.Json

data class Apartments(
    @field:Json(name = "apartments")
    val apartments: List<Apartment>
)
