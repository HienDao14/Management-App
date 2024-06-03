package com.example.managementapp.main.local.tenant

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managementapp.main.model.tenant.Tenant

@Database(
    entities = [Tenant::class],
    version = 3
)
abstract class TenantDatabase: RoomDatabase() {
    abstract fun tenantDao() : TenantDao
}