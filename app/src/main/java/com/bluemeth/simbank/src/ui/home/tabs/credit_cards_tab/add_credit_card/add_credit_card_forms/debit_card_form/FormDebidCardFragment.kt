package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFormDebidCardBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form.model.FormDebitCard
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FormDebidCardFragment : Fragment() {
    private lateinit var binding: FragmentFormDebidCardBinding
    private val formDebitCardViewModel: FormDebitCardViewModel by viewModels()
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormDebidCardBinding.inflate(inflater,container,false)

        initUI()
        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }


    private fun initListeners() {

        with(binding) {
            inputAliasText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputAliasText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputAliasText.onTextChanged { onFieldChanged() }

            inputPinText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputPinText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputPinText.onTextChanged { onFieldChanged() }

                formDebitCardViewModel.onFieldsChanged(
                    FormDebitCard(
                        alias = inputAliasText.text.toString(),
                        pin = inputPinText.text.toString()
                    )
                )
            }

        }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            formDebitCardViewModel.viewState.collect { viewState ->
                updateUI(viewState)

                val solicitar = requireActivity().findViewById<Button>(R.id.btnSolicitarDebit)
                solicitar.isVisible = viewState.isFormDebitCardValidated()
                newDebitCard()
            }
        }
    }

    private fun updateUI(viewState: FormDebitCardViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)
        binding.inputPin.error =
            if (viewState.isValidPin) null else "Debe contener 4 caracteres"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            formDebitCardViewModel.onFieldsChanged(
                FormDebitCard(
                    alias = binding.inputAliasText.text.toString(),
                    pin  = binding.inputPinText.text.toString(),
                )
            )
        }
    }

    fun newDebitCard(){
        binding.btnSolicitarDebit.setOnClickListener(){
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }
            val alias = binding.inputAliasText.text.toString()
            val money = (1000..3000).random().toDouble()
            val pin = binding.inputPinText.text.toString().toInt()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))


            var credit = CreditCard(creditCardNumber, alias,money, pin, cvv, caducityTime, CreditCardType.Debito, "ES3372324867283267220456")
            db.collection("credit_cards").document(creditCardNumber).set(credit)

        }
    }

}