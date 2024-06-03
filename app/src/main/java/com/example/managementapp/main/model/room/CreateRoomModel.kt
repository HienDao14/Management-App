package com.example.managementapp.main.model.room

data class CreateRoomModel(
    val name: String,
    val elecPricePerNum: Int,
    val pricePerMonth: Int,
    val waterPricePerMonth: Int,
    val area: Double?,
    val description: String?
)
