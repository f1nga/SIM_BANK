package com.bluemeth.simbank.src.data.models

import com.google.firebase.Timestamp

data class Movement(
    val title: String,
    val date: Timestamp,
    val price: Double,
    val isIncome: Boolean,
    val owner: String
)
