package com.example.managementapp.main.model.bill

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "bills")
data class Bill(
    @field:Json(name = "_id")
    @PrimaryKey
    val billId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "old_water_num")
    val oldWaterNumber: String,

    @ColumnInfo(name = "new_water_num")
    val newWaterNumber: String,

    @ColumnInfo(name = "old_electric_num")
    val oldElectricNumber: String,

    @ColumnInfo(name = "new_electric_num")
    val newElectricNumber: String,

    @ColumnInfo(name = "water_num")
    val waterNumber: Int,

    @ColumnInfo(name = "electric_num")
    val electricNumber: Int,

    @ColumnInfo(name = "room_price")
    val roomPrice: Int,

    @ColumnInfo(name = "electric_price")
    val electricPrice: Int,

    @ColumnInfo(name = "water_price")
    val waterPrice: Int,

    @ColumnInfo(name = "total_price")
    val total: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "status")
    val status: Boolean,

    @ColumnInfo(name = "paid_at")
    val paidAt: String?,

    @ColumnInfo(name = "bill_images")
    val billImages : String?,

    @ColumnInfo(name = "room_name")
    val roomName : String,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "addition_services")
    val additionServices: String?,

    @field:Json(name = "room")
    @ColumnInfo(name = "roomId")
    val roomId: String,

    @field:Json(name = "apartment")
    @ColumnInfo(name = "apartmentId")
    val apartmentId: String
)
