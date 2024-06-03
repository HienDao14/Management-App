package com.example.managementapp.main.local.record

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.main.model.record.Record

@Dao
interface RecordDao {
    @Upsert
    suspend fun upsertRecords(records: List<Record>)

    @Upsert
    suspend fun upsertRecord(record: Record)

    @Query("SELECT * FROM records WHERE recordId = :recordId")
    suspend fun getRecord(recordId: String) : Record

    @Query("SELECT * FROM records WHERE room_id = :roomId")
    suspend fun getRecords(roomId: String): List<Record>

    @Query("SELECT * FROM records WHERE room_id = :roomId AND is_the_last")
    suspend fun getTheLastRecord(roomId: String): Record

    @Query("DELETE FROM records WHERE recordId = :recordId")
    suspend fun deleteRecord(recordId: String)

}