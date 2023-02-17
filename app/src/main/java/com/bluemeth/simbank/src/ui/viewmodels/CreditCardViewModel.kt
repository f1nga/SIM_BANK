package com.bluemeth.simbank.src.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.firebase.CardRepository


public class CreditCardViewModel : ViewModel() {

    private val creditCardRepository = CardRepository()

    fun fetchCardData():LiveData<MutableList<CreditCard>>{
        var mutableData = MutableLiveData<MutableList<CreditCard>>()
        creditCardRepository.getCardData().observeForever {
            mutableData.value = it
        }
        return mutableData
    }
}