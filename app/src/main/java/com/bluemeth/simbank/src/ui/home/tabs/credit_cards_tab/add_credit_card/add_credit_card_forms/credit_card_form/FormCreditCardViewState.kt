package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

class FormCreditCardViewState(
    val isValidMoney: Boolean = true,
    val isValidAlias: Boolean = true,
    val isValidPin : Boolean = true,
) {
    fun isFormCreditCardValidated() = isValidMoney && isValidAlias && isValidPin
}