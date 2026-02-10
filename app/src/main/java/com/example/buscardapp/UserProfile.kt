package com.example.buscardapp

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserProfile(
    val id: String? = null,
    @SerialName("first_name") val firstName: String? = "", // Mapeia a coluna da DB
    @SerialName("last_name") val lastName: String? = ""
)