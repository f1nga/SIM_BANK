package com.bluemeth.simbank.src.data.models

data class User(
    val email: String,
    val password: String,
    val name: String,
    val phone: Int,
    val image: String
)
