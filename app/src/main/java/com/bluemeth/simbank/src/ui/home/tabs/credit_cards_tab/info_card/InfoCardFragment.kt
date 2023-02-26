package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.info_card

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentInfoCardBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InfoCardFragment() : Fragment() {
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentInfoCardBinding
    lateinit var imgCard : ImageView
    lateinit var type : Editable

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoCardBinding.inflate(inflater,container,false)
        imgCard = binding.imageView

        fillInfoCard()

        binding.btnDeleteCard.setOnClickListener {
            showQuestionDialog(it, creditCardViewModel.getCard()?.number!!)

        }

        return binding.root
    }

    private fun showQuestionDialog(view: View, cardNumber: String) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = getString(R.string.dialog_question_sure),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(getString(R.string.dialog_question_help_text), Toast.LENGTH_LONG)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                creditCardViewModel.deleteCardFromDB(cardNumber)
                it.dismiss()
                view.findNavController().navigate(R.id.action_infoCardFragment_to_cardFragment)
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun fillInfoCard(){
        fillEditables()
        loadCardImg()
    }

    private fun fillEditables(){
        val inputText = Editable.Factory.getInstance()
        val cardNumber = Methods.formatCardNumber(creditCardViewModel.getCard()?.number.toString())
        type = inputText.newEditable(creditCardViewModel.getCard()?.type.toString())
        val caducity = Methods.formatDateCardInfo(creditCardViewModel.getCard()?.caducity!!.toDate())
        val caducityCard = Methods.formatDateCard(creditCardViewModel.getCard()?.caducity!!.toDate())
        homeViewModel.getUserName().observe(requireActivity()) {
            binding.inputCardNameText.text = inputText.newEditable(it.name)
        }
        //binding.inputCardAliasText.text = inputText.newEditable(creditCardViewModel.getCard()?.number.toString())
        binding.inputCardTypeText.text = type
        binding.inputCardCaducityText.text = inputText.newEditable(caducity)
        binding.inputCardPinText.text = inputText.newEditable(creditCardViewModel.getCard()?.pin.toString())
        binding.inputCardCvvText.text = inputText.newEditable(creditCardViewModel.getCard()?.cvv.toString())

        homeViewModel.getUserName().observe(requireActivity()) {
            binding.textViewName.text = it.name
        }
        binding.textViewNumber.text = cardNumber
        binding.textViewCaducity.text = caducityCard
    }

    private fun loadCardImg(){
        val img = type.toString()
        if(img == "Prepago"){
            imgCard.setImageResource(R.drawable.visaprepago)
        }else if(img == "Debito"){
            imgCard.setImageResource(R.drawable.visadebito)
        }else if(img == ("Credito")){
            imgCard.setImageResource(R.drawable.visacredito)
        }
    }



}