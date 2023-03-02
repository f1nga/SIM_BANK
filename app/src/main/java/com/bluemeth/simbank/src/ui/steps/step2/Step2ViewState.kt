package com.bluemeth.simbank.src.ui.steps.step2

class Step2ViewState (
    val isButtonIbanClicked : Boolean = false,
    val isButtonMoneyClicked: Boolean = false,
    val isValidAlias: Boolean = true
    ){
    fun isStep2Validated() = isButtonIbanClicked && isButtonMoneyClicked && isValidAlias
}

