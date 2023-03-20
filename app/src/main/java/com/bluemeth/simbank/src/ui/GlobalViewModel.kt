package com.bluemeth.simbank.src.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val movementsRepository: MovementRepository
) : ViewModel() {

    fun getUserAuth(): FirebaseUser {
        return authenticationRepository.getCurrentUser()
    }

    fun getUserName(): LiveData<String> {
        val name = MutableLiveData<String>()

        getUserFromDB().observeForever() {
            name.value = it.name
        }

        return name
    }

    fun getUserByEmail(email: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(email)
            .observeForever {
                user.value = it
            }

        return user
    }

    fun getUserByName(name: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByName(name)
            .observeForever {
                user.value = it
            }

        return user
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

        userRepository.findUserByEmail(authenticationRepository.getCurrentUser().email!!)
            .observeForever {
                user.value = it
            }

        return user
    }

    fun getBankAccountFromDB(): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(authenticationRepository.getCurrentUser().email!!)
            .observeForever {
                bankAccount.value = it
            }

        return bankAccount
    }

    fun getBankAccountFromDBbyPhone(phoneNumber: Int): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        userRepository.findUserByPhoneNumber(phoneNumber).observeForever {
            bankAccountRepository.findBankAccountByEmail(it.email).observeForever { bank ->
                bankAccount.value = bank
            }
        }

        return bankAccount
    }

    fun getBankAccountFromDBbyIban(iban: String): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByIban(iban).observeForever { bank ->
            bankAccount.value = bank
        }

        return bankAccount
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMovementsFromDB2(email: String, iban: String): MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        viewModelScope.launch {
            var listData = mutableListOf<Movement>()
            movementsRepository.getSendedMovements(email).observeForever {
                listData = it
            }
            movementsRepository.getReceivedMovements(iban).observeForever {
                for (movement in it) {
                    listData.add(movement)
                }
            }

            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            listData.sortByDescending {
                LocalDate.parse(Methods.parseDateToString(it.date.toDate()), dateTimeFormatter)
            }

            mutableData.value = listData
        }

        return mutableData
    }

    fun getSendedMovementsFromDB(): MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        viewModelScope.launch {
            movementsRepository.getSendedMovements(getUserAuth().email!!).observeForever {
                mutableData.value = it
            }
        }

        return mutableData
    }

    fun getReceivedMovementsFromDB(): MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        getBankIban().observeForever {
            viewModelScope.launch {
                movementsRepository.getReceivedMovements(it).observeForever {
                    mutableData.value = it
                }
            }

        }

        return mutableData
    }
}