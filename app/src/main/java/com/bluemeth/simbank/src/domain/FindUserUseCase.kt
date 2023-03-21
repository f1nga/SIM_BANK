package com.bluemeth.simbank.src.domain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class FindUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository
) {

    suspend operator fun invoke(
        names: String,
    ): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        val bank = MutableLiveData<BankAccount>()
        val userList = mutableListOf<User>()
        val listData = MutableLiveData<List<User>>()

        Log.i("hool100", names)

            userRepository.findUserByName2(names).observeForever {
                Log.i("hool110", it.toString())
                user.value = it
//                    bankAccountRepository.findBankAccountByEmail(it.email).observeForever {
//                        Log.i("hool120",it.toString())
//                        bank.value = it
//
//                    }


            }

        return user


    }
}