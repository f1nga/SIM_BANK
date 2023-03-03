package com.bluemeth.simbank.src.ui.steps.step4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.domain.CreateUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class Step4ViewModel @Inject constructor(private val createUserDataUseCase: CreateUserDataUseCase): ViewModel() {
    private lateinit var _newCreditCard : CreditCard

    private lateinit var _newBankAccount : BankAccount
    val newBankAccount: BankAccount
        get() = _newBankAccount

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    fun setNewCreditCard(creditCard: CreditCard) {
        _newCreditCard = creditCard
    }

    fun setNewBankAccount(bankAccount: BankAccount) {
        _newBankAccount = bankAccount
    }

    fun insertDataToDB() {
        viewModelScope.launch {
            val userDataCreated = createUserDataUseCase.invoke(_newBankAccount, _newCreditCard)

            if(userDataCreated) {
                _navigateToHome.value = Event(true)
            } else {
                Timber.tag("ERROR").w("Cannot create user data")
            }
        }
    }
}