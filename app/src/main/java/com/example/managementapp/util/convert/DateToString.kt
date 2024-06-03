package com.example.managementapp.util.convert

fun String.toDateString(): String{
    val day = this.substring(0,2)
    val month = this.substring(3,5)
    val year = this.substring(6,10)
    return "$day$month$year"
}