package com.bluemeth.simbank.src.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.HorizontalListRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    val headerAdapter: HorizontalListRVAdapter,
   // val movementAdapter: MovementRecordsRVAdapter,
    ) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _money = MutableLiveData<Double>()
    val money: LiveData<Double>
        get() = _money

    init {
        setMoney()
    }

    fun onLogoutSelected() {
        logoutUser()
    }

    private fun logoutUser() {
        authenticationRepository.logout()
        _navigateToLogin.value = Event(true)
        prefs.clearPrefs()
    }

    fun getUserName(): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(prefs.getEmail()).observeForever {
            user.value = it
        }

        return user
    }

    fun setMoney() {
        getBankAccount().observeForever() {
            _money.value = it.money
        }
    }

     fun getBankAccount(): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(prefs.getEmail()).observeForever {
            bankAccount.value = it
        }

        return bankAccount
    }
}