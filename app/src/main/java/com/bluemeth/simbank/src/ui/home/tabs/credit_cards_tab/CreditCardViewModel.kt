package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import android.util.Log
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
    private val bankAccountRepository: BankAccountRepository,
    private val insertCreditCardUseCase: InsertCreditCardUseCase,
): ViewModel() {
    private var _creditCard : CreditCard? = null
    fun getCreditsCardsFromDB(iban: String):LiveData<MutableList<CreditCard>>{
        val mutableData = MutableLiveData<MutableList<CreditCard>>()

        creditCardRepository.getCreditCards(iban).observeForever {
            mutableData.value = it
        }
        return mutableData
    }

    fun insertCreditCardToDB(creditCard: CreditCard) {
        viewModelScope.launch { insertCreditCardUseCase(creditCard) }
    }

    fun setCard(creditCard: CreditCard){
    _creditCard = creditCard
    }

    fun getCard(): CreditCard?{
        return _creditCard
    }

    fun getNameUserCard(email: String) : LiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(email).observeForever {
            user.value = it
        }

        return user
    }

    fun getBankAccount(email: String): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(email).observeForever {
            bankAccount.value = it
        }

        return bankAccount
    }
}