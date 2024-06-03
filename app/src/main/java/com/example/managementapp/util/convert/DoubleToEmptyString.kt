package com.example.managementapp.util.convert

fun Double?.tToEmptyString(): String {
    if(this == null || this == 0.0){
        return ""
    }
    return this.toString()
}