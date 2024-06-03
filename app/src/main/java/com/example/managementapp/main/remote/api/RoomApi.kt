package com.example.managementapp.main.remote.api

import com.example.managementapp.main.model.room.CreateRoomModel
import com.example.managementapp.main.model.room.Room
import com.example.managementapp.main.model.room.RoomForBill
import com.example.managementapp.main.model.room.Rooms
import com.example.managementapp.main.model.room.RoomsWithRecord
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomApi {

    @GET("room/get")
    suspend fun getRooms(
        @Query("apartmentId") apartmentId: String
    ): Rooms

    @GET("room/get/{id}")
    suspend fun getRoomById(
        @Path("id") roomId: String,
        @Query("apartmentId") apartmentId: String
    ): Room

    @GET("room/get-records")
    suspend fun getRoomsWithRecord(
        @Query("apartmentId") apartmentId: String
    ): RoomsWithRecord

    @GET("room/get-for-bill")
    suspend fun getRoomForBill(
        @Query("roomId") roomId: String
    ): RoomForBill

    @POST("room/create")
    suspend fun createRoom(
        @Query("apartmentId") apartmentId: String,
        @Body body: CreateRoomModel
    )
    @POST("room/update")
    suspend fun updateRoom(
        @Query("apartmentId") apartmentId: String,
        @Query("roomId") roomId: String,
        @Body body: CreateRoomModel
    )
    @DELETE("room/delete/{id}")
    suspend fun deleteRoom(
        @Path("id") roomId: String,
        @Query("apartmentId") apartmentId: String
    )
}