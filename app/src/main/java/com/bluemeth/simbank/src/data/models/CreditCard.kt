package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.google.firebase.Timestamp

data class CreditCard(
    val number: String,
    val money: Double,
    val pin: Int,
    val cvv: Int,
    val caducity: Timestamp,
    val type: CreditCardType,
    val bank_iban: String
)
