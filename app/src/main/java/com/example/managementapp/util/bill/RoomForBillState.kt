package com.example.managementapp.util.bill

import com.example.managementapp.main.model.room.RoomForBill

data class RoomForBillState(
    val records: RoomForBill? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
