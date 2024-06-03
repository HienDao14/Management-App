package com.example.managementapp.main.model.record

import androidx.room.ColumnInfo
import com.example.managementapp.main.model.room.RoomWithNameOnly
import com.squareup.moshi.Json

data class RecordWithRoomInfo(
    @field:Json(name = "_id")
    val recordId: String,
    val waterNumber: String,
    val electricNumber: String,
    val recordedAt: String,
    val recordImages: String,
    val isTheLast: Boolean,
    val room: RoomWithNameOnly
)
