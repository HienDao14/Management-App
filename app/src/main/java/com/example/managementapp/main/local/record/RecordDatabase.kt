package com.example.managementapp.main.local.record

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managementapp.main.model.record.Record

@Database(
    entities = [Record::class],
    version = 1
)
abstract class RecordDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
}