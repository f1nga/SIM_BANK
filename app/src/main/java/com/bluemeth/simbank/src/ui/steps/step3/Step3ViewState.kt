package com.bluemeth.simbank.src.ui.steps.step3

data class Step3ViewState(
    val isButtonClicked: Boolean = false,
    val isValidAlias: Boolean = true,
    val isValidPin: Boolean = true

) {
    fun isStep3Validated() = isButtonClicked && isValidAlias && isValidPin
}