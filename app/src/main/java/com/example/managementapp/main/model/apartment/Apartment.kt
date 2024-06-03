package com.example.managementapp.main.model.apartment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.Date

@Entity(tableName = "apartments")
data class Apartment(

    @PrimaryKey
    @field:Json(name = "_id")
    val apartmentId: String,

    @ColumnInfo(name = "apartment_name")
    val name: String,

    @ColumnInfo(name = "room_count")
    val roomCount: Int,

    @ColumnInfo(name = "room_empty")
    val roomEmpty : Int,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "getBillDate", defaultValue = "1")
    val getBillDate: Int?,

    @ColumnInfo(name = "desc", defaultValue = "")
    val description: String,

    @ColumnInfo(name = "default_elec_price")
    val defaultElecPrice: Int?,

    @ColumnInfo(name = "default_water_price")
    val defaultWaterPrice: Int?,

    @ColumnInfo(name = "default_living_price")
    val defaultRoomPrice: Int?,

    @ColumnInfo(name = "userId")
    val user: String
)