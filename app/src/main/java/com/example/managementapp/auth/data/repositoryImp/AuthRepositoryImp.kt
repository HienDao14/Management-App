package com.example.managementapp.auth.data.repositoryImp

import android.content.SharedPreferences
import android.util.Log
import com.example.managementapp.auth.data.local.UserDatabase
import com.example.managementapp.auth.data.remote.api.AuthApi
import com.example.managementapp.auth.data.remote.model.Response
import com.example.managementapp.auth.domain.model.AuthRequest
import com.example.managementapp.auth.domain.repository.AuthRepository
import com.example.managementapp.util.AuthResult
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val api: AuthApi,
    private val prefs: SharedPreferences,
    private val userDatabase: UserDatabase
) : AuthRepository {
    val userDao = userDatabase.userDao()

    override suspend fun register(
        username: String,
        password: String,
        email: String
    ): AuthResult<Unit> {
        return try {
            api.register(
                AuthRequest(username, password, email)
            )
            login(username, password)
        } catch (e: HttpException) {
            Log.d("test", "error: ${e.code()}")
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else if (e.code() == 400) {
                AuthResult.MissingInfo()
            } else if (e.code() == 402) {
                AuthResult.DuplicateUsername()
            } else AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun login(username: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.login(
                AuthRequest(username, password)
            )
            println("Login repo")
            prefs.edit()
                .putString("accessToken", response.accessToken)
                .putString("user_id", response.user.userId)
                .apply()
            userDao.upsertUser(response.user)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                println(e.message)
                AuthResult.Unauthorized()
            } else if (e.code() == 400) {
                println(e.message)
                AuthResult.MissingInfo()
            } else if (e.code() == 403) {
                println(e.message)
                AuthResult.IncorrectInfo()
            } else{
                println(e.message)
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            println(e.message)
            AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString("accessToken", null) ?: return AuthResult.Unauthorized()

            if(!prefs.getBoolean("network_status", false)){
                AuthResult.Authorized(null)
            }

            val response = api.authenticate()
            val user = response.user
            prefs.edit().putString("user_id", user.userId).apply()

            userDao.upsertUser(response.user)
            AuthResult.Authorized()
        } catch (e: Exception) {
            Log.d("test", "fail" + e.message)
            AuthResult.DuplicateUsername()
        }
    }

    override suspend fun getOTP(email: String): Response {
        return try {
            api.getOTP(email)
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
            Response(false, e.message.toString())
        }
    }

    override suspend fun verifyOTP(email: String, otp: String): Response {
        return try {
            api.verifyOTP(email, otp)
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
            Response(false, e.message.toString())
        }
    }

    override suspend fun updatePassword(email: String, password: String): Response {
        return try {
            api.updatePassword(email, password)
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
            Response(false, e.message.toString())
        }
    }
}