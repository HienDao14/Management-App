package com.example.managementapp.main.repository.apartment

import android.util.Log
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.local.apartment.ApartmentDatabase
import com.example.managementapp.main.remote.api.ApartmentApi
import com.example.managementapp.main.model.apartment.Apartments
import com.example.managementapp.main.model.apartment.CreateApartmentModel
import com.example.managementapp.main.model.apartment.UpdateApartmentModel
import com.example.managementapp.util.MyResource
import retrofit2.HttpException
import javax.inject.Inject

class ApartmentRepositoryImp @Inject constructor(
    private val apartmentApi: ApartmentApi,
    private val apartmentDatabase: ApartmentDatabase
) : ApartmentRepository {
    private val apartmentDao = apartmentDatabase.apartmentDao()

    override suspend fun getApartments(shouldMakeNetworkRequest: Boolean?, userId: String): MyResource<List<Apartment>>{
        return try {
            if(shouldMakeNetworkRequest == true){
                getApartmentsFromNetwork()
            } else {
                getApartmentsFromDb(userId)
            }
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getApartmentsFromDb(userId: String): MyResource<List<Apartment>> {
        return try {
            MyResource.Success(
                data = apartmentDao.getAllApartment(userId)
            )
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getApartmentsFromNetwork(): MyResource<List<Apartment>> {
        return try {
            val apartments = apartmentApi.getAllApartments().apartments
            apartmentDao.upsertApartments(apartments)
            MyResource.Success(
                data = apartments
            )
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getApartmentById(
        shouldMakeNetworkRequest: Boolean?,
        userId: String,
        apartmentId: String
    ): MyResource<Apartment> {
        return try {
            if(shouldMakeNetworkRequest == true){
                getApartmentFromNetwork(apartmentId)
            } else {
                getApartmentByIdLocal(userId, apartmentId)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getApartmentFromNetwork(apartmentId: String): MyResource<Apartment> {
        return try {
            val apartment = apartmentApi.getApartmentById(apartmentId).apartment
            apartmentDao.upsertApartment(apartment)
            MyResource.Success(
                data = apartment
            )
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getApartmentByIdLocal(
        userId: String,
        apartmentId: String
    ): MyResource<Apartment> {
        return try {
            val apartment = apartmentDao.getApartment(apartmentId, userId)
            MyResource.Success(
                data = apartment
            )
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun createApartment(
        name: String,
        address: String,
        roomCount: Int,
        isCreate: Boolean,
        defaultElecPrice: Int?,
        defaultWaterPrice: Int?,
        defaultRoomPrice: Int?
    ): MyResource<Unit> {
        return try {
            apartmentApi.createApartment(
                body = CreateApartmentModel(
                    name = name,
                    roomCount = roomCount,
                    address = address,
                    description = "",
                    area = 0.0,
                    defaultRoomPrice = defaultRoomPrice,
                    defaultElecPrice =  defaultElecPrice,
                    defaultWaterPrice = defaultWaterPrice
                ),
                isCreate = isCreate.toString()
            )
            MyResource.Success(Unit)
        } catch (e: HttpException) {
            Log.d("test", e.message())
            MyResource.Error(e.message())
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun updateApartment(
        apartmentId: String,
        apartment: UpdateApartmentModel
    ): MyResource<Unit> {
        return try {
            apartmentApi.updateApartment(
                apartmentId = apartmentId,
                apartment
            )
            MyResource.Success(Unit)
        }catch (e: HttpException){
            Log.d("test", e.message())
            MyResource.Error(e.message())
        } catch (e: Exception){
            Log.d("test", e.message.toString())
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteApartment(apartmentId: String): MyResource<Unit> {
        return try {
            apartmentApi.deleteApartment(apartmentId)
            apartmentDao.deleteApartment(apartmentId)
            MyResource.Success(Unit)
        }catch (e: Exception){
            Log.d("test", e.message.toString())
            MyResource.Error(e.message.toString())
        }
    }
}