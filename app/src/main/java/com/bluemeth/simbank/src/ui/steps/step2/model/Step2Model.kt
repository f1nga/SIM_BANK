package com.bluemeth.simbank.src.ui.steps.step2.model

data class Step2Model(
    val alias: String,
    val isGeneratedIBANButtonClicked: Boolean = false,
    val isGeneratedMoneyButtonClicked: Boolean = false
) {
    fun isNotEmpty() = alias.isNotEmpty() && isGeneratedIBANButtonClicked && isGeneratedMoneyButtonClicked
}
