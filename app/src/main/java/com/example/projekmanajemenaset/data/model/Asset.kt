package com.example.projekmanajemenaset.data.model

data class Asset(
    val id: Int = 0,
    val name: String,
    val category: String,
    val location: String,
    val quantity: Int,
    val condition: String,
    val purchaseDate: String,
    val description: String
)