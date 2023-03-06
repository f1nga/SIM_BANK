package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.domain.InsertCreditCardUseCase
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model.FormCreditCard
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FormCreditCardViewModel @Inject constructor(
    private val insertCreditCardUseCase: InsertCreditCardUseCase
) : ViewModel() {
    private companion object {
        const val FIELD_LENGTH = 4
        const val MIN_IMPORT = 300.0
        const val MAX_IMPORT = 50000.0
    }

    private val _viewState = MutableStateFlow(FormCreditCardViewState())
    val viewState: StateFlow<FormCreditCardViewState>
        get() = _viewState

    private val _navigateToCards = MutableLiveData<Event<Boolean>>()
    val navigateToCards: LiveData<Event<Boolean>>
        get() = _navigateToCards

    fun onFieldsChanged(formCreditCard: FormCreditCard) {
        _viewState.value = formCreditCard.toFormCreditCardModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH || alias.isEmpty()
    fun isValidMoney(money: String): Boolean {
        if(money.isNotEmpty()){
            return money.toDouble() in MIN_IMPORT..MAX_IMPORT
        }
        return money.isEmpty()
    }
    fun isValidNumber(number: String) = number != "0000 0000 0000 0000"
    fun isValidPin(pin: String) = pin.length == FIELD_LENGTH || pin.isEmpty()


    fun FormCreditCard.toFormCreditCardModelState(): FormCreditCardViewState {
        return FormCreditCardViewState(
            isValidMoney = isValidMoney(money),
            isValidAlias = isValidAlias(alias),
            isValidPin = isValidPin(pin)
        )
    }

    fun onFinishSelected(context: Context, iban: String, formCreditCard: FormCreditCard) {
        val viewState = formCreditCard.toFormCreditCardModelState()
        if (viewState.isFormCreditCardValidated() && formCreditCard.isNotEmpty()) {
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }
            val alias = formCreditCard.alias
            val money = formCreditCard.money.toDouble()
            val pin = formCreditCard.pin.toInt()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))

            val creditCard = CreditCard(
                creditCardNumber,
                alias,
                money,
                pin,
                cvv,
                caducityTime,
                CreditCardType.Credito,
                iban
            )
            viewModelScope.launch {
                val cardInserted = insertCreditCardUseCase(creditCard)
                if (cardInserted) {
                    _navigateToCards.value = Event(true)
                }
            }
        } else {
            onFieldsChanged(formCreditCard)
            Toast.makeText(context, "¡Porfavor, completa los campos antes de solicitar la nueva tarjeta!", Toast.LENGTH_SHORT).show()
        }
    }

}