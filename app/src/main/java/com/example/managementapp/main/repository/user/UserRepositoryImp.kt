package com.example.managementapp.main.repository.user

import com.example.managementapp.auth.data.local.User
import com.example.managementapp.auth.data.local.UserDatabase
import com.example.managementapp.main.remote.api.UserApi
import com.example.managementapp.main.repository.user.UserRepository
import com.example.managementapp.util.MyResource
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import com.hadiyarajesh.flower_core.dbResource
import com.hadiyarajesh.flower_core.networkResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val userDatabase: UserDatabase,
    private val userApi : UserApi

): UserRepository {
    private val userDao = userDatabase.userDao()

    override suspend fun getUser(
        userId: String
    ): MyResource<User> {
        return try {
            MyResource.Success(
                data = userApi.getUser()
            )
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getUserFromDb(userId: String): MyResource<User> {
        return try {
            MyResource.Success(
                data = userDao.getUser(userId)
            )
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getUserFromNetwork(): MyResource<User> {
        return try {
            MyResource.Success(
                data = userApi.getUser()
            )
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }
}