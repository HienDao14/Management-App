package com.example.managementapp.main.model.tenant

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.Date

@Entity(tableName = "tenant")
data class Tenant(
    @field:Json(name = "_id")
    @PrimaryKey
    @ColumnInfo(name = "tenant_id")
    val tenantId: String,

    @ColumnInfo(name = "tenant_name")
    val fullName: String,

    @ColumnInfo(name = "tenant_gender")
    val gender: String,

    @field:Json(name = "phoneNum")
    @ColumnInfo(name = "tenant_phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "dob")
    val dob: String,

    @ColumnInfo(name = "place_of_origin")
    val placeOfOrigin: String,

    @field:Json(name = "identityCardNum")
    @ColumnInfo(name = "identity_card")
    val identityCardNumber: String,

    @ColumnInfo(name = "identity_card_images")
    val identityCardImages: String?,

    @ColumnInfo(name = "deposit")
    val deposit: Int,

    @ColumnInfo(name = "start_date")
    val startDate: String,

    @ColumnInfo(name = "end_date")
    val endDate: String?,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "roomName")
    val roomName: String,

    @field:Json(name = "room")
    @ColumnInfo(name = "room_id")
    val roomId: String,

    @field:Json(name = "apartment")
    @ColumnInfo(name = "apartment_id", defaultValue = "")
    val apartmentId: String
)
