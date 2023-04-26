package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.utils.Constants

data class User(
    val email: String,
    val password: String,
    val name: String,
    val phone: Int,
    val image: String = Constants.DEFAULT_PROFILE_IMAGE,
    val level: Int = 1,
    val exp: Int = 0,
    val missions_completed : MutableList<String> = mutableListOf(),
    val contacts : MutableList<String> = mutableListOf(),
    val blocked_contacts: MutableList<String> = mutableListOf(),
)
