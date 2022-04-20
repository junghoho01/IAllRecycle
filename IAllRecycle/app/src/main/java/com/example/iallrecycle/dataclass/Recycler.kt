package com.example.iallrecycle.dataclass

data class Recycler(
    val companyEmail: String ?= "",
    val companyName: String ?= "",
    val contactNo: String ?= "",
    val latitude: String?= "",
    val longitude: String?= "",
    val ownerName: String?= "",
    val profileImage: String?= "",
    val timeStamp: String?= "",
    val uid: String?= "",
    val userType: String?= ""
)
