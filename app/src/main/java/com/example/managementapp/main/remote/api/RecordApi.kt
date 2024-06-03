package com.example.managementapp.main.remote.api

import com.example.managementapp.main.model.record.Record
import com.example.managementapp.main.model.record.Records
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RecordApi {
    @GET("record/get")
    suspend fun getRecords(
        @Query("roomId") roomId: String
    ): Records

    @GET("record/get")
    suspend fun getTheLastRecord(
        @Query("roomId") roomId: String,
        @Query("getLast") getLast: Boolean
    ): Record

    @GET("record/get")
    suspend fun getRecord(
        @Query("recordId") recordId: String
    ): Record

    @POST("record/create")
    @Multipart
    suspend fun createRecord(
        @Query("roomId") roomId: String,
        @Part electricNumber: MultipartBody.Part,
        @Part waterNumber: MultipartBody.Part,
        @Part recordedAt: MultipartBody.Part,
        @Part isTheLast: MultipartBody.Part,
        @Part photos: List<MultipartBody.Part>?
    )

    @POST("record/update")
    @Multipart
    suspend fun updateRecord(
        @Query("roomId") roomId: String,
        @Query("recordId") recordId : String,
        @Part electricNumber: MultipartBody.Part,
        @Part waterNumber: MultipartBody.Part,
        @Part recordedAt: MultipartBody.Part,
        @Part photos: List<MultipartBody.Part>?
    )

    @DELETE("record/delete")
    suspend fun deleteRecord(
        @Query("recordId") recordId: String
    )
}