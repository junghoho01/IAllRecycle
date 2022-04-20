package com.example.iallrecycle.dataclass

data class RecycleWaste(
    val glass: String ?= "",
    val plastic: String ?= "",
    val paper: String ?= "",
    val others: String ?= "",
    val recylerEmail: String ?= "",
    val date: String?= "",
    val timestamp: String?= ""
)

