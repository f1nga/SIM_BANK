package com.bluemeth.simbank.src.data.models

data class BankAccount(
    val iban: String,
    val money: Double,
    val userEmail: String
)
