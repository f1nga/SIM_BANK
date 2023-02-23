package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.model.CreditCardInfo
import com.bluemeth.simbank.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCreditCardViewModel @Inject constructor(
    val cardAdapter: AddCreditCardRVAdapter
) : ViewModel() {

    private val _creditCardList = MutableLiveData<List<CreditCardInfo>>()
    val creditCardList: MutableLiveData<List<CreditCardInfo>>
        get() = _creditCardList


    fun setListData(){
        val list = mutableListOf<CreditCardInfo>(
            CreditCardInfo(R.drawable.visacredito, "Tarjeta de credito","Perfecta para viajar"," - Te permite comprar cosas sin necesidad de\n tener el dinero en ese momento \n \n - Programas de recompensas que te permiten \nacumular puntos que puedes canjear por cosas \ncomo viajes o productos.\n \n - Cada mes te enviarán una factura que te dirá \ncuánto dinero debes y cuándo tienes que pagarlo."),
            CreditCardInfo(R.drawable.visadebito, "Tarjeta de debito","Comodidad y seguridad", " - Te permite comprar cosas sin necesidad de\n tener el dinero en ese momento \n \n - Programas de recompensas que te permiten \nacumular puntos que puedes canjear por cosas \ncomo viajes o productos.\n \n - Cada mes te enviarán una factura que te dirá \ncuánto dinero debes y cuándo tienes que pagarlo."),
            CreditCardInfo(R.drawable.visaprepago, "Tarjeta prepago","Recarga y disfruta", "- Te permite comprar cosas sin necesidad de \ntener el dinero en ese momento \n \n - Programas de recompensas que te permiten \nacumular puntos que puedes canjear por cosas \ncomo viajes o productos.\n \n - Cada mes te enviarán una factura que te dirá \ncuánto dinero debes y cuándo tienes que pagarlo.")

        )
        creditCardList.value = list
    }
}