package com.example.managementapp.main.repository.room

import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.apartment.Apartments
import com.example.managementapp.main.model.room.CreateRoomModel
import com.example.managementapp.main.model.room.Room
import com.example.managementapp.main.model.room.RoomForBill
import com.example.managementapp.main.model.room.RoomWithRecord
import com.example.managementapp.main.model.room.Rooms
import com.example.managementapp.util.MyResource

interface RoomRepository {

    suspend fun getRooms(
        shouldMakeNetworkRequest: Boolean? = null,
        apartmentId: String
    ): MyResource<List<Room>>

    suspend fun getRoomsByLocal(apartmentId: String): MyResource<List<Room>>

    suspend fun getRoomByNetwork(apartmentId: String): MyResource<List<Room>>

    suspend fun getRoom(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String, roomId: String): MyResource<Room>

    suspend fun getRoomsWithRecord(apartmentId: String): MyResource<List<RoomWithRecord>>

    suspend fun getRoomForBill(roomId: String): MyResource<RoomForBill>

    suspend fun createRoom(
        apartmentId: String,
        name: String,
        roomPrice: Int,
        area: Double?,
        electricPrice: Int,
        waterPrice: Int,
        description: String?
    ): MyResource<Unit>

    suspend fun updateRoom(
        apartmentId: String,
        roomId: String,
        room: CreateRoomModel
    ): MyResource<Unit>

    suspend fun deleteRoom(apartmentId: String, roomId: String): MyResource<Unit>
}