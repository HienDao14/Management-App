package com.example.managementapp.main.model.bill

data class CreateBillModel(
    val name: String?,
    val oldElectricNumber: String?,
    val newElectricNumber: String?,
    val electricNumber: Int?,
    val electricPrice: Int?,
    val roomPrice: Int?,
    val oldWaterNumber: String?,
    val newWaterNumber: String?,
    val waterNumber: Int?,
    val waterPrice: Int?,
    val total: Int?,
    val createdAt: String?,
    val status: Boolean?,
    val paidAt: String?,
    val roomName: String?,
    val note: String?,
    val additionServices: List<AdditionService>?
)
