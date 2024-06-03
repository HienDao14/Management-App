package com.example.managementapp.main.remote.api

import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.model.bill.Bills
import com.example.managementapp.main.model.bill.CreateBillModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BillApi {

    @GET("bill/get")
    suspend fun getBillsRoom(
        @Query("roomId") roomId: String
    ): Bills

    @GET("bill/get")
    suspend fun getBillsApartment(
        @Query("apartmentId") apartmentId: String
    ): Bills

    @GET("bill/get")
    suspend fun getBill(
        @Query("billId") billId: String
    ): Bill

    @GET("bill/get-unpaid")
    suspend fun getUnpaidBillsRoom(
        @Query("roomId") roomId: String
    ): Bills

    @GET("bill/get-unpaid")
    suspend fun getUnpaidBillsApartment(
        @Query("apartmentId") apartmentId: String
    ): Bills

    @POST("bill/create")
    suspend fun createBill(
        @Query("apartmentId") apartmentId: String,
        @Query("roomId") roomId: String,
        @Body body: CreateBillModel
    )

    @POST("bill/update")
    suspend fun updateBill(
        @Query("billId") billId: String,
        @Body body: CreateBillModel
    )

    @DELETE("bill/delete")
    suspend fun deleteBill(
        @Query("billId") billId: String
    )
}