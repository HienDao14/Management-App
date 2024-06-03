package com.example.managementapp.main.model.apartment

data class UpdateApartmentModel(
    val name: String?,
    val roomCount: Int?,
    val address: String?,
    val description: String?,
    val defaultRoomPrice: Int?,
    val defaultElecPrice: Int?,
    val defaultWaterPrice: Int?
)
