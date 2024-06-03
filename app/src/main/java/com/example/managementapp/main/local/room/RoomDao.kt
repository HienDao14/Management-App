package com.example.managementapp.main.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.main.model.room.Room

@Dao
interface RoomDao {

    @Upsert
    suspend fun upsertRoom(room: Room)

    @Upsert
    suspend fun upsertRooms(rooms: List<Room>)

    @Query("SELECT * FROM room WHERE apartment_id = :apartmentId")
    suspend fun getRooms(apartmentId: String): List<Room>

    @Query("SELECT * FROM room WHERE apartment_id = :apartmentId AND roomId = :roomId")
    suspend fun getRoomById(apartmentId: String, roomId: String): Room

    @Query("DELETE FROM room WHERE apartment_id = :apartmentId AND roomId = :roomId")
    suspend fun deleteRoomById(apartmentId: String, roomId: String)

    @Query("DELETE FROM room WHERE apartment_id = :apartmentId")
    suspend fun deleteAllRoom(apartmentId: String)
}