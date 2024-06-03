package com.example.managementapp.util.convert

import java.text.SimpleDateFormat
import java.util.Date

fun Long?.toDateString(): String{
    this ?: return ""
    val date = Date(this)
    val format = SimpleDateFormat("dd/MM/yyyy")
    return format.format(date)
}