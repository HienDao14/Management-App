package com.example.managementapp.main.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managementapp.main.model.room.Room

@Database(
    entities = [Room::class],
    version = 2
)
abstract class RoomDatabase: RoomDatabase() {
    abstract fun roomDao(): RoomDao
}