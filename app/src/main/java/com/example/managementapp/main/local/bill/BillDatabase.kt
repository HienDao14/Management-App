package com.example.managementapp.main.local.bill

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managementapp.main.model.bill.Bill

@Database(
    entities = [Bill::class],
    version = 4
)
abstract class BillDatabase: RoomDatabase() {
    abstract fun billDao(): BillDao
}