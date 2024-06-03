package com.example.managementapp.main.repository.tenant

import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.util.MyResource
import java.io.File
import java.util.Date

interface TenantRepository {

    suspend fun getTenants(
        shouldMakeNetworkRequest: Boolean? = null,
        roomId: String
    ): MyResource<List<Tenant>>

    suspend fun getTenantsInApartment(
        shouldMakeNetworkRequest: Boolean? = null,
        apartmentId: String
    ): MyResource<List<Tenant>>

    suspend fun getTenant(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String, tenantId: String
    ): MyResource<Tenant>

    suspend fun createTenant(
        apartmentId: String,
        roomId: String,
        fullName: String,
        gender: String,
        phoneNumber: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImages: List<File>?
    ): MyResource<Unit>

    suspend fun updateTenant(
        tenantId: String,
        fullName: String,
        gender: String,
        phoneNumber: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImages: List<File>?
    ): MyResource<Unit>

    suspend fun deleteTenant(roomId: String, tenantId: String): MyResource<Unit>
}