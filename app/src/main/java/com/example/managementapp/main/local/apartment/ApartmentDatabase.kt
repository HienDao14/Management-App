package com.example.managementapp.main.local.apartment

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managementapp.main.model.apartment.Apartment

@Database(entities = [Apartment::class], version = 2)
abstract class ApartmentDatabase: RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao
}