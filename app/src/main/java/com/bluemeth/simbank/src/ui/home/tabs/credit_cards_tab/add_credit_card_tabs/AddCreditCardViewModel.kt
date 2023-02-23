package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card_tabs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card_tabs.model.CreditCardInfo
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardRVAdapter
import javax.inject.Inject


class AddCreditCardViewModel @Inject constructor(
    val cardAdapter: AddCreditCardRVAdapter
) : ViewModel() {

    private val _creditCardList = MutableLiveData<List<CreditCardInfo>>()
    val creditCardList: MutableLiveData<List<CreditCardInfo>>
        get() = _creditCardList


    fun setListData(){
        val list = mutableListOf<CreditCardInfo>(
            CreditCardInfo(R.drawable.visacredito, "Tarjeta de credito","hol de hol"),
            CreditCardInfo(R.drawable.visadebito, "Tarjeta de debito","hol de super"),
            CreditCardInfo(R.drawable.visaprepago, "Tarjeta prepago","hol de mega"),

        )
        creditCardList.value = list
    }
}