package com.example.managementapp.main.local.apartment

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.main.model.apartment.Apartment

@Dao
interface ApartmentDao {

    @Upsert
    suspend fun upsertApartments(
        apartments: List<Apartment>
    )

    @Upsert
    suspend fun upsertApartment(
        apartment: Apartment
    )

    @Query("SELECT * FROM apartments WHERE userId = :userId")
     fun getAllApartment(userId: String): List<Apartment>

    @Query("SELECT * FROM apartments WHERE apartmentId = :apartmentId AND userId = :userId")
     fun getApartment(apartmentId: String, userId: String): Apartment

    @Query("DELETE FROM apartments WHERE apartmentId = :apartmentId")
     fun deleteApartment(apartmentId: String)

    @Query("DELETE FROM apartments ")
     fun deleteAllApartments()

}