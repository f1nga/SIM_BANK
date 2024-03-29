package com.bluemeth.simbank.src.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.*
import com.bluemeth.simbank.src.data.providers.firebase.*
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val movementsRepository: MovementRepository,
    private val notificationRepository: NotificationRepository
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

    fun getUserFromDB(): MutableLiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.findUserByEmail(authenticationRepository.getCurrentUser().email!!)
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

    fun getBankAccountFromDB(): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        viewModelScope.launch {
            bankAccountRepository.findBankAccountByEmail(authenticationRepository.getCurrentUser().email!!)
                .observeForever {
                    bankAccount.value = it
                }
        }


        return bankAccount
    }

    fun getBankAccountFromDBbyName(name: String): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        userRepository.findUserByName(name).observeForever {
            viewModelScope.launch {
                bankAccountRepository.findBankAccountByEmail(it.email).observeForever { bank ->
                    bankAccount.value = bank
                }
            }
        }

        return bankAccount
    }

    fun getBankAccountFromDBbyPhone(phoneNumber: Int): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        userRepository.findUserByPhoneNumber(phoneNumber).observeForever {
            viewModelScope.launch {
                bankAccountRepository.findBankAccountByEmail(it.email).observeForever { bank ->
                    bankAccount.value = bank
                }
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
    fun getAllMovementsFromDB(email: String, iban: String): MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        viewModelScope.launch {
            movementsRepository.getAllMovements(email, iban).observeForever {
                mutableData.value = it
            }
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

    fun setUserMissionToDB(mission: Mission) {
        getUserFromDB().observeForever { user ->
            user.missions_completed.also { listMissions ->
                if(isMissionCompleted(mission.name, listMissions) != "") {
                    val expSum = mission.exp + user.exp

                    val exp = if (expSum >= 100) {
                        userRepository.updateUserLevel(getUserAuth().email!!, user.level+1)
                        expSum - 100
                    } else expSum

                    listMissions.add(mission.name)
                    userRepository.updateUserMissionsCompleted(getUserAuth().email!!, listMissions)
                    userRepository.updateUserExperience(getUserAuth().email!!, exp)

                }
            }
        }
    }

    private fun isMissionCompleted(mission: String, listMissions: MutableList<String>) : String {
        listMissions.forEach {
            if(it == mission) {
                return ""
            }
        }

        return mission
    }

     fun isEveryNotificationReadedFromDB(email: String) : MutableLiveData<Boolean> {
        val isReaded = MutableLiveData<Boolean>()
        viewModelScope.launch {
            notificationRepository.isEveryNotificationReaded(email).observeForever {
                isReaded.value = it

            }
        }

        return isReaded
    }

    fun getUsersFromDB(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        viewModelScope.launch {
            userRepository.getUsers(email).observeForever {
                mutableData.value = it
            }
        }

        return mutableData
    }
}