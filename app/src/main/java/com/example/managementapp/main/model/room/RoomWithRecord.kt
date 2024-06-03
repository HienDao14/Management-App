package com.example.managementapp.main.model.room

data class RoomWithRecord(
    val roomId: String,
    val roomName: String,
    val waterNumber: String,
    val electricNumber: String,
    val recordedAt: String,
    val recordId: String
)
