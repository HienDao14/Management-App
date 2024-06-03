package com.example.managementapp.util.record

import com.example.managementapp.main.model.room.RoomWithRecord

data class RoomWithRecordState(
    val records: List<RoomWithRecord>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
