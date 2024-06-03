package com.example.managementapp.auth.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "user")
data class User (
    @field:Json(name = "_id")
    @PrimaryKey
    val userId: String,

    @field:Json(name = "email")
    @ColumnInfo(name = "email")
    val email: String,

    @field:Json(name = "username")
    @ColumnInfo(name = "username")
    val username: String
)