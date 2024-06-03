package com.example.managementapp.util.convert

fun Int?.toEmptyString(): String{
    if(this == null || this == 0){
        return ""
    }
    return this.toString()
}