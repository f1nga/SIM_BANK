package com.bluemeth.simbank.src.data.models

import com.google.firebase.Timestamp

data class CreditCard(
    val number: Int,
    val money: Double,
    val pin: Int,
    val cvv: Int,
    val caducity: Timestamp,
)
