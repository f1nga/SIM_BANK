package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.info_card

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentInfoCardBinding
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoCardFragment() : Fragment() {
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()
    private lateinit var binding: FragmentInfoCardBinding
    lateinit var imgCard : ImageView
    lateinit var type : Editable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInfoCardBinding.inflate(inflater,container,false)
        imgCard = binding.imageView

        fillInfoCard()
        return binding.root
    }

    fun fillInfoCard(){
        fillEditables()
        loadCardImg()
    }

    fun fillEditables(){
        val inputText = Editable.Factory.getInstance()
        val cardNumber = Methods.formatCardNumber(creditCardViewModel.getCard()?.number.toString())
        type = inputText.newEditable(creditCardViewModel.getCard()?.type.toString())
        val caducity = Methods.formatDateCardInfo(creditCardViewModel.getCard()?.caducity!!.toDate())
        val caducityCard = Methods.formatDateCard(creditCardViewModel.getCard()?.caducity!!.toDate())
        binding.inputCardNumberText.text = inputText.newEditable(cardNumber)
        //binding.inputCardAliasText.text = inputText.newEditable(creditCardViewModel.getCard()?.number.toString())
        binding.inputCardTypeText.text = type
        binding.inputCardCaducityText.text = inputText.newEditable(caducity)
        binding.inputCardPinText.text = inputText.newEditable(creditCardViewModel.getCard()?.pin.toString())
        binding.inputCardCvvText.text = inputText.newEditable(creditCardViewModel.getCard()?.cvv.toString())
        binding.textViewNumber.text = inputText.newEditable(cardNumber)
        binding.textViewCaducity.text = inputText.newEditable(caducityCard)
    }

    fun loadCardImg(){
        val img = type.toString()
        if(img.equals("Prepago")){
            imgCard.setImageResource(R.drawable.visaprepago)
        }else if(img.equals("Debito")){
            imgCard.setImageResource(R.drawable.visadebito)
        }else if(img.equals(("Credito"))){
            imgCard.setImageResource(R.drawable.visacredito)
        }
    }



}