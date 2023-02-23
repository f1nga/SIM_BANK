package com.bluemeth.simbank.src.ui.home

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardRVAdapter
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
    ) : ViewModel() {

    private val _headerList = MutableLiveData<List<HomeHeader>>()
    val headerList: MutableLiveData<List<HomeHeader>>
        get() = _headerList

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

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

    fun setListData(){
        val list = mutableListOf<HomeHeader>(
            HomeHeader(43224.0, "Ver ahorros"),
            HomeHeader(543.5, "Ver gastos"),
            HomeHeader(53.5, "Ver gastos"),
            HomeHeader(543.5, "Ver gastos")

        )
        headerList.value = list
    }

//    fun fillHeaderList(iban: String):LiveData<MutableList<HomeHeader>>{
//        val mutableData = MutableLiveData<MutableList<HomeHeader>>()
//
//        creditCardRepository.getCreditCards(iban).observeForever {
//            mutableData.value = it
//        }
//        return mutableData
//    }

     fun getBankAccount(): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(prefs.getEmail()).observeForever {
            bankAccount.value = it
        }

        return bankAccount
    }



    fun splitName(name: String): String {
        return name.split(" ")[0]
    }
}