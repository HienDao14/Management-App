package com.example.managementapp.util

import android.os.Build
import androidx.annotation.RequiresApi

fun DateParse(date: String): String{
    val day = date.substring(0, 2)
    val month = date.substring(2, 4)
    val year = date.substring(4, date.length)
    return "${day}/${month}/${year}"
}