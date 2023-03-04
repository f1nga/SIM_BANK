package com.bluemeth.simbank.src.data.models

import com.google.firebase.Timestamp

data class Transfer(
    val beneficiary_iban: String,
    val beneficiary_name: String,
    val amount: Double,
    val subject: String,
    val date: Timestamp = Timestamp.now(),
    val isIncome: Boolean = false,
    val remaining_money: Double = 0.0,
    val user_email: String
)
