package com.bluemeth.simbank.src.data.models

data class User(
    val email: String,
    val password: String,
    val name: String,
    val phone: Int,
    val image: String = "gs://simbank-334b7.appspot.com/images/profile/defaultprofile.png",
    val missions_completed : List<String> = emptyList()
)
