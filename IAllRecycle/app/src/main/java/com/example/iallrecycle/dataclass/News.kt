package com.example.iallrecycle.dataclass

data class News(
    val title: String ?= null,
    val date: String ?= null,
    val details: String ?= null,
    val pictureName: String?= null
)
