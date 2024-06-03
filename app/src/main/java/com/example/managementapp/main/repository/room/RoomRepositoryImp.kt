package com.example.managementapp.main.repository.room

import android.util.Log
import com.example.managementapp.main.local.room.RoomDatabase
import com.example.managementapp.main.model.apartment.CreateApartmentModel
import com.example.managementapp.main.model.room.CreateRoomModel
import com.example.managementapp.main.model.room.Room
import com.example.managementapp.main.model.room.RoomForBill
import com.example.managementapp.main.model.room.RoomWithRecord
import com.example.managementapp.main.model.room.Rooms
import com.example.managementapp.main.remote.api.RoomApi
import com.example.managementapp.util.MyResource
import retrofit2.HttpException
import javax.inject.Inject

class RoomRepositoryImp @Inject constructor(
    val roomDatabase: RoomDatabase,
    val roomApi: RoomApi
): RoomRepository{
    val roomDao = roomDatabase.roomDao()

    override suspend fun getRooms(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String
    ): MyResource<List<Room>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val list = roomApi.getRooms(apartmentId)
                roomDao.upsertRooms(list.rooms)
                MyResource.Success(data = list.rooms)
            } else {
                val list = roomDao.getRooms(apartmentId)
                MyResource.Success(data = list)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getRoomsByLocal(apartmentId: String): MyResource<List<Room>> {
        return try {
            val list = roomDao.getRooms(apartmentId)
            MyResource.Success(list)
        }catch (e: Exception){
            MyResource.Error(message = e.message.toString())
        }
    }

    override suspend fun getRoomByNetwork(apartmentId: String): MyResource<List<Room>> {
        return try {
            val list = roomApi.getRooms(apartmentId)
            MyResource.Success(list.rooms)
        }catch (e: Exception){
            MyResource.Error(message = e.message.toString())
        }
    }

    override suspend fun getRoom(shouldMakeNetworkRequest: Boolean?, apartmentId: String, roomId: String): MyResource<Room> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val room = roomApi.getRoomById(roomId, apartmentId)
                MyResource.Success(data = room)
            } else {
                val room = roomDao.getRoomById(apartmentId, roomId)
                MyResource.Success(data = room)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getRoomsWithRecord(apartmentId: String): MyResource<List<RoomWithRecord>> {
        return try {
            val rooms = roomApi.getRoomsWithRecord(apartmentId).rooms
            MyResource.Success(rooms)
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getRoomForBill(roomId: String): MyResource<RoomForBill> {
        return try {
            val rooms = roomApi.getRoomForBill(roomId)
            MyResource.Success(rooms)
        }catch (e: Exception){
            MyResource.Error(
                e.message.toString()
            )
        }
    }

    override suspend fun createRoom(
        apartmentId: String,
        name: String,
        roomPrice: Int,
        area: Double?,
        electricPrice: Int,
        waterPrice: Int,
        description: String?
    ): MyResource<Unit> {
        return try {
            roomApi.createRoom(
                apartmentId = apartmentId,
                body = CreateRoomModel(
                    name = name,
                    electricPrice,
                    roomPrice,
                    waterPrice,
                    area = area,
                    description = description
                )
            )
            MyResource.Success(Unit)
        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun updateRoom(
        apartmentId: String,
        roomId: String,
        room: CreateRoomModel
    ): MyResource<Unit> {
        return try {
            roomApi.updateRoom(apartmentId = apartmentId, roomId = roomId, body = room)
            MyResource.Success(Unit)
        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteRoom(apartmentId: String, roomId: String): MyResource<Unit> {
        return try {
            roomApi.deleteRoom(roomId, apartmentId)
            roomDao.deleteRoomById(apartmentId, roomId)
            MyResource.Success(Unit)
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }
}