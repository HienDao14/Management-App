package com.example.managementapp.main.repository.record

import androidx.room.ColumnInfo
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.record.Record
import com.example.managementapp.util.MyResource
import com.squareup.moshi.Json
import java.io.File

interface RecordRepository {

    suspend fun getRecords(
        shouldMakeNetworkRequest: Boolean? = null,
        roomId: String
    ): MyResource<List<Record>>

    suspend fun getRecordsFromDb(roomId: String): MyResource<List<Record>>
    suspend fun getRecordsFromNetwork(roomId: String): MyResource<List<Record>>

    suspend fun getRecord(
        shouldMakeNetworkRequest: Boolean? = null,
        recordId: String
    ): MyResource<Record>

    suspend fun getTheLastRecord(
        shouldMakeNetworkRequest: Boolean? = null,
        roomId: String
    ): MyResource<Record>

    suspend fun createRecord(
        roomId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        isTheLast: Boolean,
        recordImages : List<File>?
    ): MyResource<Unit>

    suspend fun updateRecord(
        roomId: String,
        recordId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        recordImages : List<File>?
    ): MyResource<Unit>

    suspend fun deleteRecord(
        recordId: String
    ): MyResource<Unit>
}