package com.example.managementapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun CompareDateString(lastDate: String, newDate: String): Boolean{
    println(lastDate + " " + newDate)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
    val dateTime1 = LocalDate.parse(lastDate, formatter)
    val dateTime2 = LocalDate.parse(newDate, formatter)
    return dateTime1.isBefore(dateTime2)
}