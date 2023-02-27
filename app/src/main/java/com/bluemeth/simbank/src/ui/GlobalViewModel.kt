package com.bluemeth.simbank.src.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.utils.GlobalVariables
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class GlobalViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    ): ViewModel() {

     fun getUserName(): LiveData<String> {
         val name = MutableLiveData<String>()

         getUserFromDB().observeForever() {
             name.value = it.name
         }

         return name
    }

     fun getBankMoney(): MutableLiveData<Double> {
        val money = MutableLiveData<Double>()

        getBankAccountFromDB().observeForever() {
            money.value = it.money
        }

        return money
    }

     fun getBankIban(): MutableLiveData<String> {
        val iban = MutableLiveData<String>()

        getBankAccountFromDB().observeForever() {
            iban.value = it.iban
        }

        return iban
    }

     fun getUserFromDB(): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(GlobalVariables.userEmail!!).observeForever {
            user.value = it
        }

        return user
    }

     fun getBankAccountFromDB(): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(GlobalVariables.userEmail!!).observeForever {
            bankAccount.value = it
        }

        return bankAccount
    }
}