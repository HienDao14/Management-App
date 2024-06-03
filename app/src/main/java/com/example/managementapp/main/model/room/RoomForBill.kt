package com.example.managementapp.main.model.room

import com.example.managementapp.main.model.record.Record

data class RoomForBill(
    val room: Room,
    val records: List<Record>
)
