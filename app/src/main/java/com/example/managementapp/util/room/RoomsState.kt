package com.example.managementapp.util.room

import com.example.managementapp.main.model.room.Room

data class RoomsState(
    val rooms: List<Room>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)