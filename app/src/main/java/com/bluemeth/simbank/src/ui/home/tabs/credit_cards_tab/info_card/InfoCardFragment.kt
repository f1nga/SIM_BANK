package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.info_card

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InfoCardFragment() : Fragment() {

    private val creditCardViewModel: CreditCardViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private lateinit var binding: FragmentInfoCardBinding
    private lateinit var creditCard : CreditCard
    private lateinit var type : Editable

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoCardBinding.inflate(inflater,container,false)

        creditCard = creditCardViewModel.creditCard!!

        fillInfoCard()
        initListeners()

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

        val cardNumber = Methods.formatCardNumber(creditCard.number)
        val caducity = Methods.formatDateCardInfo(creditCard.caducity.toDate())
        val caducityCard = Methods.formatDateCard(creditCard.caducity.toDate())

        type = inputText.newEditable(creditCard.type.toString())

        globalViewModel.getUserName().observe(requireActivity()) {
            binding.inputCardNameText.text = inputText.newEditable(it)
            binding.textViewName.text = it
        }

        binding.inputCardAliasText.text = inputText.newEditable(creditCard.alias)
        binding.inputCardTypeText.text = type
        binding.inputCardCaducityText.text = inputText.newEditable(caducity)
        binding.inputCardPinText.text = inputText.newEditable(creditCard.pin.toString())
        binding.inputCardCvvText.text = inputText.newEditable(creditCard.cvv.toString())

        binding.textViewNumber.text = cardNumber
        binding.textViewCaducity.text = caducityCard
    }

    private fun loadCardImg(){
        val imgCard = binding.imageView

        when(type.toString()) {
            CreditCardType.Credito.toString() -> imgCard.setImageResource(R.drawable.visacredito)
            CreditCardType.Debito.toString() -> imgCard.setImageResource(R.drawable.visadebito)
            CreditCardType.Prepago.toString() -> imgCard.setImageResource(R.drawable.visaprepago)
        }
    }

    private fun initListeners() {
        binding.btnDeleteCard.setOnClickListener {
            showQuestionDialog(it, creditCard.number)
        }
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_info_card)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_infoCardFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}