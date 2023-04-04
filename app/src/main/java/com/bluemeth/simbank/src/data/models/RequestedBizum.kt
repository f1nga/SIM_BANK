package com.bluemeth.simbank.src.data.models

import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.Timestamp

data class RequestedBizum(
    val id: String = Methods.generateToken(),
    val beneficiary_iban: String,
    val beneficiary_name: String,
    val amount: Double,
    val subject: String,
    val date: Timestamp = Timestamp.now(),
    val user_email: String
)
