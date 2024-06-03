package com.example.managementapp.main.repository.apartment

import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.apartment.Apartments
import com.example.managementapp.main.model.apartment.UpdateApartmentModel
import com.example.managementapp.util.MyResource

interface ApartmentRepository {
    suspend fun getApartments(
        shouldMakeNetworkRequest: Boolean? = null,
        userId: String
    ): MyResource<List<Apartment>>

    suspend fun getApartmentsFromDb(userId: String): MyResource<List<Apartment>>
    suspend fun getApartmentsFromNetwork(): MyResource<List<Apartment>>

    suspend fun getApartmentById(
        shouldMakeNetworkRequest: Boolean? = null,
        userId: String,
        apartmentId: String
    ): MyResource<Apartment>

    suspend fun getApartmentFromNetwork(apartmentId: String): MyResource<Apartment>

    suspend fun getApartmentByIdLocal(userId: String, apartmentId: String): MyResource<Apartment>

    suspend fun createApartment(
        name: String,
        address: String,
        roomCount: Int,
        isCreate: Boolean,
        defaultElecPrice: Int?,
        defaultWaterPrice: Int?,
        defaultRoomPrice: Int?
    ): MyResource<Unit>

    suspend fun updateApartment(
        apartmentId: String,
        apartment: UpdateApartmentModel
    ): MyResource<Unit>

    suspend fun deleteApartment(
        apartmentId: String
    ): MyResource<Unit>
}