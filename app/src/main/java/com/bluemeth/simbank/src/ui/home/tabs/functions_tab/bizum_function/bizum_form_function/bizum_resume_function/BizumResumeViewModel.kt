package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.Bizum
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.domain.FindBankAccountsUseCase
import com.bluemeth.simbank.src.domain.FindUserUseCase
import com.bluemeth.simbank.src.domain.InsertBizumUseCase
import com.bluemeth.simbank.src.domain.InsertTransferUseCase
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BizumResumeViewModel @Inject constructor(
    val agendaRVAdapter: AgendaRVAdapter,
    private val insertTransferUseCase: InsertTransferUseCase,
    private val insertBizumUseCase: InsertBizumUseCase,
    private val findUserUseCase: FindUserUseCase,
    private val findBankAccountsUseCase: FindBankAccountsUseCase,
    private val bankAccountRepository: BankAccountRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository

) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    fun makeBizum(
        iban: String,
        movement: Movement,
        beneficiaryMoney: Double,
        beneficiaryIban: String
    ) {
        viewModelScope.launch {

            val bizumCreated =
                insertTransferUseCase(iban, movement, beneficiaryMoney, beneficiaryIban)

            if (bizumCreated) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }

        }
    }

    fun setBizumMissionToDB(email:String, ) {

    }

    fun makeBizum(iban: String, movement: Bizum, beneficiariesMoney: List<Double>) {
        viewModelScope.launch {

            val bizumCreated = insertBizumUseCase(iban, movement, beneficiariesMoney)

            if (bizumCreated) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
        }
    }

    fun getBeneficiarysAccount(names: List<String>): MutableLiveData<List<BankAccount>> {

        val beneficiariesBanksAccount = MutableLiveData<List<BankAccount>>()
        val bankList = mutableListOf<BankAccount>()
        viewModelScope.launch {
            for (name in names) {
                Log.i("hool10", name)

                    findUserUseCase(name).observeForever { user ->
                            bankAccountRepository.findBankAccountByEmail2(user.email).observeForever {bank ->
                                bankList.add(bank)
                                Log.i("hool11", name)
                            }

                    }
                }
            Log.i("hool12", "shill")


            beneficiariesBanksAccount.value = bankList

        }
        return beneficiariesBanksAccount
    }
}