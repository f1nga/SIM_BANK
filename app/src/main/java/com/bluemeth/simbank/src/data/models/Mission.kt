package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.utils.Methods

data class Mission(
    val id: String = Methods.generateToken(),
    val name: String,
    val exp : Int,
    val done : Boolean = false
)
