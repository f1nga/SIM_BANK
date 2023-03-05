package com.bluemeth.simbank.src.data.models

import com.google.firebase.Timestamp

data class TargetPay(
    val beneficiary_name: String,
    val date: Timestamp,
    val price: Double,
    val isIncome: Boolean,
    val owner: String
)
