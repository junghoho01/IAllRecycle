package com.example.iallrecycle.dataclass

data class User(
    val email: String ?= "",
    val name: String ?= "",
    val phone: String ?= "",
    val profileImage: String ?= "",
    val timeStamp: String ?= "",
    val uid: String ?= "",
    val userType: String ?= "",
    val points: String ?= ""
)
