package com.example.managementapp.main.local.bill

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.main.model.bill.Bill

@Dao
interface BillDao {

    @Upsert
    suspend fun upsertBills(bills: List<Bill>)

    @Upsert
    suspend fun upsertBill(bill: Bill)

    @Query("SELECT * FROM bills WHERE billId = :billId")
    suspend fun getBill(billId: String): Bill

    @Query("SELECT * FROM bills WHERE roomId = :roomId")
    suspend fun getBillsRoom(roomId: String): List<Bill>

    @Query("SELECT * FROM bills WHERE apartmentId = :apartmentId")
    suspend fun getBillsApartment(apartmentId: String): List<Bill>

    @Query("SELECT * FROM bills WHERE roomId = :roomId AND status = 0")
    suspend fun getUnpaidBillsRoom(roomId: String): List<Bill>

    @Query("SELECT * FROM bills WHERE apartmentId = :apartmentId AND status = 0")
    suspend fun getUnpaidBillsApartment(apartmentId: String): List<Bill>

    @Query("DELETE FROM bills WHERE billId = :billId")
    suspend fun deleteBill(billId: String)
}