package com.example.managementapp.util.bill

import com.example.managementapp.main.model.bill.Bill

data class BillsState(
    val bills: List<Bill>? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
