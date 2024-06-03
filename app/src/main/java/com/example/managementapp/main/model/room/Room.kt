package com.example.managementapp.main.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "room")
data class Room (
    @field:Json(name = "_id")
    @PrimaryKey
    val roomId: String,

    @field:Json(name = "name")
    @ColumnInfo(name = "room_name")
    val name: String,

    @field:Json(name = "tenantNumber")
    @ColumnInfo(name = "tenant_number")
    val tenantNumber: Int,

    @field:Json(name = "elecPricePerNum")
    @ColumnInfo(name = "elec_price")
    val elecPricePerMonth: Int,

    @field:Json(name = "pricePerMonth")
    @ColumnInfo(name = "living_price")
    val livingPricePerMonth: Int,

    @field:Json(name = "waterPricePerMonth")
    @ColumnInfo(name = "water_price")
    val waterPricePerMonth: Int,

    @field:Json(name = "available")
    @ColumnInfo(name = "available")
    val available: Boolean,

    @ColumnInfo(name = "unpaid_bill")
    val unpaidBill: Int?,

    @field:Json(name = "area")
    @ColumnInfo(name = "area")
    val area: Double?,

    @field:Json(name = "description")
    @ColumnInfo(name = "description")
    val description: String?,

    @field:Json(name = "apartment")
    @ColumnInfo(name = "apartment_id")
    val apartmentId: String
)