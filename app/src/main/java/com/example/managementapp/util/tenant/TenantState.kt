package com.example.managementapp.util.tenant

import com.example.managementapp.main.model.tenant.Tenant

data class TenantState(
    val tenants: List<Tenant>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
