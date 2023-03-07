package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.google.firebase.Timestamp

data class Movement(
    val beneficiary_iban: String,
    val beneficiary_name: String,
    val amount: Double,
    val subject: String,
    val date: Timestamp = Timestamp.now(),
    val isIncome: Boolean = false,
    val payment_type: PaymentType,
    val remaining_money: Double = 0.0,
    val user_email: String
)
