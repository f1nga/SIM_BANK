package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

class FormCreditCardViewState(
    val isButtonNumberClicked : Boolean = false,
    val isButtonMoneyClicked: Boolean = false,
    val isValidAlias: Boolean = false
) {
    fun isFormCreditCardValidated() = isButtonNumberClicked && isButtonMoneyClicked && isValidAlias
}