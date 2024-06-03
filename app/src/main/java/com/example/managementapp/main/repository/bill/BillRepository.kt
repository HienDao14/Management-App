package com.example.managementapp.main.repository.bill

import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.model.bill.CreateBillModel
import com.example.managementapp.util.MyResource

interface BillRepository {

    suspend fun createBill(
        apartmentId: String,
        roomId: String,
        bill: CreateBillModel
    ): MyResource<Unit>

    suspend fun getBillsRoom(
        shouldMakeNetworkRequest: Boolean? = null,
        roomId: String
    ): MyResource<List<Bill>>

    suspend fun getBillsApartment(
        shouldMakeNetworkRequest: Boolean? = null,
        apartmentId: String
    ): MyResource<List<Bill>>

    suspend fun getUnpaidBillsRoom(
        shouldMakeNetworkRequest: Boolean? = null,
        roomId: String
    ): MyResource<List<Bill>>

    suspend fun getUnpaidBillsApartment(
        shouldMakeNetworkRequest: Boolean? = null,
        apartmentId: String
    ): MyResource<List<Bill>>

    suspend fun getBill(
        shouldMakeNetworkRequest: Boolean? = null,
        billId: String
    ): MyResource<Bill>

    suspend fun deleteBill(
        billId: String
    ): MyResource<Unit>

    suspend fun updateBill(
        billId: String,
        bill: CreateBillModel
    ): MyResource<Unit>
}