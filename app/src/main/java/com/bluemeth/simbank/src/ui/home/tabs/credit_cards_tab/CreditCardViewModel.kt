package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import androidx.lifecycle.*
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.domain.InsertCreditCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val creditCardRepository: CreditCardRepository,
    val cardAdapter: CreditCardRVAdapter,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var _creditCard: CreditCard? = null
    val creditCard get() = _creditCard

    fun getCreditsCardsFromDB(iban: String): LiveData<MutableList<CreditCard>> {
        val mutableData = MutableLiveData<MutableList<CreditCard>>()

        creditCardRepository.getCreditCards(iban).observeForever {
            mutableData.value = it
        }
        return mutableData
    }

    fun setCard(creditCard: CreditCard) {
        _creditCard = creditCard
    }

    fun getNameUserCard(email: String): LiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(email).observeForever {
            user.value = it
        }

        return user
    }

    fun deleteCardFromDB(cardNumber: String) {
        creditCardRepository.deleteCreditCard(cardNumber)
    }
}