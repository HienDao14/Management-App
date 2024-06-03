package com.example.managementapp.main.repository.bill

import com.example.managementapp.main.local.bill.BillDatabase
import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.model.bill.CreateBillModel
import com.example.managementapp.main.remote.api.BillApi
import com.example.managementapp.util.MyResource
import javax.inject.Inject

class BillRepositoryImp @Inject constructor(
    private val billDatabase: BillDatabase,
    private val billApi: BillApi
): BillRepository {
    private val billDao = billDatabase.billDao()

    override suspend fun createBill(
        apartmentId: String,
        roomId: String,
        bill: CreateBillModel
    ): MyResource<Unit> {
        return try {
            billApi.createBill(
                apartmentId,
                roomId,
                bill
            )
            MyResource.Success(Unit)
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun getBillsRoom(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ): MyResource<List<Bill>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val bills = billApi.getBillsRoom(roomId).bills
                billDao.upsertBills(bills)
                MyResource.Success(bills)
            } else {
                val bills = billDao.getBillsRoom(roomId)
                MyResource.Success(bills)
            }
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun getBillsApartment(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String
    ): MyResource<List<Bill>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val bills = billApi.getBillsApartment(apartmentId).bills
                billDao.upsertBills(bills)
                MyResource.Success(bills)
            } else {
                val bills = billDao.getBillsApartment(apartmentId)
                MyResource.Success(bills)
            }
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun getUnpaidBillsRoom(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ): MyResource<List<Bill>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val bills = billApi.getUnpaidBillsRoom(roomId).bills
                billDao.upsertBills(bills)
                MyResource.Success(bills)
            } else {
                val bills = billDao.getUnpaidBillsRoom(roomId)
                MyResource.Success(bills)
            }
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun getUnpaidBillsApartment(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String
    ): MyResource<List<Bill>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val bills = billApi.getUnpaidBillsApartment(apartmentId).bills
                billDao.upsertBills(bills)
                MyResource.Success(bills)
            } else {
                val bills = billDao.getUnpaidBillsApartment(apartmentId)
                MyResource.Success(bills)
            }
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteBill(billId: String): MyResource<Unit> {
        return try {
            billApi.deleteBill(billId)
            billDao.deleteBill(billId)
            MyResource.Success(Unit)
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun updateBill(
        billId: String,
        bill: CreateBillModel
    ): MyResource<Unit> {
        return try {
            billApi.updateBill(billId, bill)
            MyResource.Success(Unit)
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun getBill(
        shouldMakeNetworkRequest: Boolean?,
        billId: String
    ): MyResource<Bill> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val bill = billApi.getBill(billId)
                billDao.upsertBill(bill)
                MyResource.Success(bill)
            } else {
                val bill = billDao.getBill(billId)
                MyResource.Success(bill)
            }
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }
}