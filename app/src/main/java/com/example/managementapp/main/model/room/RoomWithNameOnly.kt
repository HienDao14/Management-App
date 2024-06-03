package com.example.managementapp.main.model.room

import com.squareup.moshi.Json

data class RoomWithNameOnly(
    @field:Json(name = "_id")
    val roomId: String,
    val name: String
)
