package com.example.managementapp.util.record

import com.example.managementapp.main.model.record.Record

data class RecordsState(
    val records: List<Record>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
