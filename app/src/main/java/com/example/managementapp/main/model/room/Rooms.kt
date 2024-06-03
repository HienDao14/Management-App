package com.example.managementapp.main.model.room

import com.squareup.moshi.Json

data class Rooms(
    @field:Json(name = "rooms")
    val rooms: List<Room>
)