package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form

class FormPrepaidCardViewState(
    val isValidAlias: Boolean = true,
    val isValidPin: Boolean = true,
    val isValidMoney : Boolean = true
) {
    fun isFormPrepaidCardValidated() = isValidPin && isValidAlias && isValidMoney
}