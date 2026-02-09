package com.example.buscardapp

import kotlinx.serialization.Serializable

@Serializable
data class BusRoute(
    val id: String? = null,
    val origin: String,
    val destination: String,
    val duration: String? = null,
    val price: Double? = null
)