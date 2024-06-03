package com.example.managementapp.main.remote.api

import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.apartment.ApartmentGet
import com.example.managementapp.main.model.apartment.Apartments
import com.example.managementapp.main.model.apartment.CreateApartmentModel
import com.example.managementapp.main.model.apartment.UpdateApartmentModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApartmentApi{

    @GET("apartment/get")
    suspend fun getAllApartments(): Apartments

    @GET("apartment/get/{apartmentId}")
    suspend fun getApartmentById(
        @Path("apartmentId") apartmentId: String
    ): ApartmentGet

    @POST("apartment/create")
    suspend fun createApartment(
        @Query("isCreate") isCreate: String,
        @Body body: CreateApartmentModel
    )

    @POST("apartment/update")
    suspend fun updateApartment(
        @Query("apartmentId") apartmentId: String,
       @Body body: UpdateApartmentModel
    )

    @DELETE("apartment/delete")
    suspend fun deleteApartment(
        @Query("apartmentId") apartmentId: String
    )
}