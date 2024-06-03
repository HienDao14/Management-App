package com.example.managementapp.main.model.tenant

import com.squareup.moshi.Json

data class Tenants(
    @field:Json(name = "tenants")
    val tenants: List<Tenant>
)
