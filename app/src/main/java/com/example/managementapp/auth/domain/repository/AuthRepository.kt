package com.example.managementapp.auth.domain.repository

import com.example.managementapp.auth.data.remote.model.Response
import com.example.managementapp.util.AuthResult

interface AuthRepository {

    suspend fun register(username: String, password: String, email: String): AuthResult<Unit>

    suspend fun login(username: String, password: String): AuthResult<Unit>

    suspend fun authenticate(): AuthResult<Unit>

    suspend fun getOTP(email: String): Response

    suspend fun verifyOTP(email: String, otp: String): Response

    suspend fun updatePassword(email: String, password: String): Response
}