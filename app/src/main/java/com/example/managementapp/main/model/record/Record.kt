package com.example.managementapp.main.model.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "records")
data class Record(
    @PrimaryKey
    @field:Json(name = "_id")
    val recordId: String,

    @ColumnInfo(name = "water_number")
    val waterNumber: String,

    @ColumnInfo(name = "electric_number")
    val electricNumber: String,

    @ColumnInfo(name = "recorded_at")
    val recordedAt: String,

    @ColumnInfo(name = "record_images")
    val recordImages: String,

    @ColumnInfo(name = "is_the_last")
    val isTheLast: Boolean,

    @ColumnInfo(name = "room_id")
    @field:Json(name = "room")
    val roomId: String
)