package com.example.managementapp.main.local.tenant

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.managementapp.main.model.apartment.Apartment
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.model.tenant.Tenants

@Dao
interface TenantDao {

    @Upsert
    suspend fun upsertTenant(tenant: Tenant)

    @Upsert
    suspend fun upsertTenants(tenants: List<Tenant>)

    @Query("SELECT * FROM tenant WHERE room_id = :roomId")
    suspend fun getTenants(roomId: String): List<Tenant>

    @Query("SELECT * FROM tenant WHERE apartment_id = :apartmentId")
    suspend fun getTenantsInApartment(apartmentId: String): List<Tenant>

    @Query("SELECT * FROM tenant WHERE room_id = :roomId AND tenant_id = :tenantId")
    suspend fun getTenantById(roomId: String, tenantId: String): Tenant

    @Query("DELETE FROM tenant WHERE room_id = :roomId AND tenant_id = :tenantId")
    suspend fun deleteTenantById(roomId: String, tenantId: String)
}