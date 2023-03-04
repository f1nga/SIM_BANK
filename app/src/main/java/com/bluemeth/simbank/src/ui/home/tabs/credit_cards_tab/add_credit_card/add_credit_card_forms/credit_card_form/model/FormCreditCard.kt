package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model

class FormCreditCard(
    var isGeneratedNumberButtonClicked : Boolean = false,
    var isGeneratedMoneyButtonClicked : Boolean = false,
    var alias : String
){
    fun isNotEmpty() = alias.isNotEmpty() && isGeneratedNumberButtonClicked && isGeneratedMoneyButtonClicked

}