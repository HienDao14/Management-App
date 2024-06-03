package com.example.managementapp.main.repository.user

import com.example.managementapp.auth.data.local.User
import com.example.managementapp.util.MyResource
import com.hadiyarajesh.flower_core.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): MyResource<User>

    suspend fun getUserFromDb(userId: String): MyResource<User>
    suspend fun getUserFromNetwork(): MyResource<User>
}