package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model

data class FormCreditCard(
    var money : String,
    var alias : String,
    var pin : String,
){
    fun isNotEmpty() = alias.isNotEmpty() && money.isNotEmpty() && pin.isNotEmpty()

}