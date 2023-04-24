package com.bluemeth.simbank.src.data.models

data class User(
    val email: String,
    val password: String,
    val name: String,
    val phone: Int,
    val image: String = "https://firebasestorage.googleapis.com/v0/b/simbank-334b7.appspot.com/o/images%2Fprofile%2Fdefaultprofile.png?alt=media&token=97421ce8-993a-40eb-9c20-2f9d2cc8178f",
    val level: Int = 1,
    val exp: Int = 0,
    val missions_completed : MutableList<String> = mutableListOf(),
    val contacts : MutableList<String> = mutableListOf(),

)
