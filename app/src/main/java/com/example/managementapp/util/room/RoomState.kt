package com.example.managementapp.util.room

import com.example.managementapp.main.model.room.Room

data class RoomState(
    val room: Room? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
