package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form

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
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form.model.FormDebitCard
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
@HiltViewModel
class FormDebitCardViewModel @Inject constructor(
    private val insertCreditCardUseCase: InsertCreditCardUseCase
) : ViewModel() {
    private companion object {
        const val FIELD_LENGTH = 4
    }

    private val _viewState = MutableStateFlow(FormDebitCardViewState())
    val viewState: StateFlow<FormDebitCardViewState>
        get() = _viewState

    private val _navigateToCards = MutableLiveData<Event<Boolean>>()
    val navigateToCards: LiveData<Event<Boolean>>
        get() = _navigateToCards

    fun onFieldsChanged(formDebitCard: FormDebitCard) {
        _viewState.value = formDebitCard.toFormDebitCardModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH || alias.isEmpty()
    fun isValidPin(alias: String) = alias.length == FIELD_LENGTH || alias.isEmpty()

    fun FormDebitCard.toFormDebitCardModelState(): FormDebitCardViewState {
        return FormDebitCardViewState(
            isValidAlias = isValidAlias(alias),
            isValidPin = isValidPin(pin)
        )
    }

    fun onFinishSelected(context: Context,iban: String, formDebitCard: FormDebitCard) {
        val viewState = formDebitCard.toFormDebitCardModelState()
        if (viewState.isFormDebitCardValidated() && formDebitCard.isNotEmpty()) {
            var debitCardNumber = ""

            for (i in 1..16) {
                debitCardNumber += (0..9).random()
            }
            val alias = formDebitCard.alias
            val money = (1000..3000).random().toDouble()
            val pin = formDebitCard.pin.toInt()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))

            val creditCard = CreditCard(
                debitCardNumber,
                alias,
                money,
                pin,
                cvv,
                caducityTime,
                CreditCardType.Debito,
                iban
            )
            viewModelScope.launch {
                val cardInserted = insertCreditCardUseCase(creditCard)
                if (cardInserted) {
                    _navigateToCards.value = Event(true)
                }
            }
        } else {
            onFieldsChanged(formDebitCard)
            Toast.makeText(context, "¡Porfavor, completa los campos antes de solicitar la nueva tarjeta!", Toast.LENGTH_SHORT).show()
        }
    }


}