package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.Timestamp

data class Bizum(
    val id: String = Methods.generateToken(),
    val beneficiary_iban: List<String>,
    val beneficiary_name: List<String>,
    val amount: Double,
    val subject: String,
    val category: String,
    val date: Timestamp = Timestamp.now(),
    val isIncome: Boolean = false,
    val payment_type: PaymentType,
    val remaining_money: Double = 0.0,
    val beneficiary_remaining_money: List<Double>,
    val user_email: String
)
