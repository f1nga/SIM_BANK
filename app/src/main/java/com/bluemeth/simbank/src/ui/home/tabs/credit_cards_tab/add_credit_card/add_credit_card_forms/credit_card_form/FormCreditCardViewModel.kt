package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model.FormCreditCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FormCreditCardViewModel @Inject constructor() : ViewModel() {
    private companion object {
        const val FIELD_LENGTH = 4
    }

    private val _viewState = MutableStateFlow(FormCreditCardViewState())
    val viewState: StateFlow<FormCreditCardViewState>
        get() = _viewState

    fun onFieldsChanged(formCreditCard: FormCreditCard) {
        _viewState.value = formCreditCard.toFormCreditCardModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH

    fun FormCreditCard.toFormCreditCardModelState(): FormCreditCardViewState {
        return FormCreditCardViewState(
            isButtonNumberClicked = isGeneratedNumberButtonClicked,
            isButtonMoneyClicked = isGeneratedMoneyButtonClicked,
            isValidAlias = isValidAlias(alias)
        )
    }

}