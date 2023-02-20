package com.bluemeth.simbank.src.ui.home.tabs.cards_tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val creditCardRepository: CreditCardRepository,
    val cardAdapter: CardRVAdapter
    ): ViewModel() {

    fun fetchCardData():LiveData<MutableList<CreditCard>>{
        val mutableData = MutableLiveData<MutableList<CreditCard>>()

        creditCardRepository.getCreditCards().observeForever {
            mutableData.value = it
        }
        return mutableData
    }
}