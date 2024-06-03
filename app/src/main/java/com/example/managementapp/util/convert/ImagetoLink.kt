package com.example.managementapp.util.convert

fun String.toLinkImage(): String{
    return "http://localhost:5000/api/v1/$this"
}