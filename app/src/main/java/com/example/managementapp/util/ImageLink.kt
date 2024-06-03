package com.example.managementapp.util

import com.example.managementapp.util.constant.LocalhostConst

fun ImageLink(imagePath: String): String {
    val ipAddress = LocalhostConst.ipAddress
    return "http://${ipAddress}:5000/api/v1/${imagePath}"
}