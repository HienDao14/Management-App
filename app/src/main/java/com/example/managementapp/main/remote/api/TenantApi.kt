package com.example.managementapp.main.remote.api

import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.model.tenant.Tenants
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.POST


interface TenantApi {

    @POST("tenant/create")
    @Multipart
    suspend fun createTenant(
        @Query("apartmentId") apartmentId: String,
        @Query("roomId") roomId: String,
        @Part fullName: MultipartBody.Part,
        @Part gender: MultipartBody.Part,
        @Part dob: MultipartBody.Part,
        @Part phoneNum: MultipartBody.Part,
        @Part placeOfOrigin: MultipartBody.Part,
        @Part identityCard: MultipartBody.Part,
        @Part deposit: MultipartBody.Part,
        @Part startDate: MultipartBody.Part,
        @Part endDate: MultipartBody.Part?,
        @Part note: MultipartBody.Part?,
        @Part roomName: MultipartBody.Part,
        @Part photos: List<MultipartBody.Part>?
    )

    @POST("tenant/update")
    @Multipart
    suspend fun updateTenant(
        @Query("tenantId") tenantId: String,
        @Part fullName: MultipartBody.Part,
        @Part gender: MultipartBody.Part,
        @Part dob: MultipartBody.Part,
        @Part phoneNum: MultipartBody.Part,
        @Part placeOfOrigin: MultipartBody.Part,
        @Part identityCard: MultipartBody.Part,
        @Part deposit: MultipartBody.Part,
        @Part startDate: MultipartBody.Part,
        @Part endDate: MultipartBody.Part?,
        @Part note: MultipartBody.Part?,
        @Part roomName: MultipartBody.Part,
        @Part photos: List<MultipartBody.Part>?
    )

    @GET("tenant/get")
    suspend fun getTenants(
        @Query("roomId") roomId: String
    ): Tenants

    @GET("tenant/get-apartment")
    suspend fun getTenantsInApartment(
        @Query("apartmentId") apartmentId: String
    ): Tenants

    @GET("tenant/get/{id}")
    suspend fun getTenantsById(
        @Path("id") tenantId: String,
        @Query("roomId") roomId: String,
    ): Tenant

    @DELETE("tenant/delete/{id}")
    suspend fun deleteTenantById(
        @Path("id") tenantId: String,
        @Query("roomId") roomId: String
    )
}