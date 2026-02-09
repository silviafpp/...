package com.example.buscardapp

import kotlinx.serialization.Serializable

@Serializable
data class UserCard(
    val id: String? = null,
    val user_id: String,
    val card_type: String,
    val is_active: Boolean = true
)