package com.example.managementapp.main.model.apartment

data class CreateApartmentModel(
    val name: String,
    val roomCount: Int,
    val address: String,
    val description: String?,
    val area: Double?,
    val defaultRoomPrice: Int?,
    val defaultElecPrice: Int?,
    val defaultWaterPrice: Int?
)