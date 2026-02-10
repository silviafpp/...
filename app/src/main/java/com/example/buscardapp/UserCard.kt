package com.example.buscardapp

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserCard(
    @SerialName("user_id") val userId: String? = null,
    val saldo: Double? = 0.0,
    @SerialName("trips_left") val tripsLeft: Int? = 0, // Mapeia trips_left
    @SerialName("total_trips") val totalTrips: Int? = 0
)