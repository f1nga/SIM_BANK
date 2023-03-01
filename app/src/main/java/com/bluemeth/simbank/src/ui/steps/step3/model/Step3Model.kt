package com.bluemeth.simbank.src.ui.steps.step3.model

data class Step3Model(
    val alias: String,
    val pin: String
) {
    fun isNotEmpty() = alias.isNotEmpty() && pin.isNotEmpty()
}
