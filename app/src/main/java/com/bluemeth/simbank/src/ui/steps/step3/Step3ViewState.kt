package com.bluemeth.simbank.src.ui.steps.step3

data class Step3ViewState(
    val isButtonClicked: Boolean = false,
    val isValidAlias: Boolean = false,
    val isValidPin: Boolean = false

) {
    fun isStep3Validated() = isButtonClicked && isValidAlias && isValidPin
}