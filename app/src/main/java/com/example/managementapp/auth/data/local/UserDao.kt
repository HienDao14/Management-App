package com.example.managementapp.auth.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.auth.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: User)

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUser(userId: String): User

    @Query("DELETE FROM user WHERE userId = :userId")
    fun deleteUser(userId: String)
}