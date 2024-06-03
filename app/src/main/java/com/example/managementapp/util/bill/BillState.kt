package com.example.managementapp.util.bill

import com.example.managementapp.main.model.bill.Bill
import com.example.managementapp.main.model.room.Room

data class BillState(
    val bill: Bill? = null,
    val isLoading: Boolean = false,
    val error: String ?= null
)
