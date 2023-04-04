package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFormCreditCardBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model.FormCreditCard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentFormCreditCardBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val formCreditCardViewModel: FormCreditCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormCreditCardBinding.inflate(inflater, container, false)

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

            inputMoneyText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputMoneyText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputMoneyText.onTextChanged { onFieldChanged() }

            inputPinText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputPinText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputPinText.onTextChanged { onFieldChanged() }


            btnSolicitarCredit.setOnClickListener {
                globalViewModel.getBankIban().observe(requireActivity()) {
                    formCreditCardViewModel.onFinishSelected(
                        requireContext(), it, FormCreditCard(
                            alias = inputAliasText.text.toString(),
                            money = inputMoneyText.text.toString(),
                            pin = inputPinText.text.toString(),
                        )
                    )
                }
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            formCreditCardViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
        formCreditCardViewModel.navigateToCards.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_formCreditCardFragment_to_cardFragment)
                toast("¡Muy bien! Acabas de solicitar tu nueva tarjeta!")
            }
        }
    }

    private fun updateUI(viewState: FormCreditCardViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)
        binding.inputMoney.error =
            if (viewState.isValidMoney) null else "La tarjeta tiene que tener entre 300 y 50.000€"
        binding.inputPin.error = if (viewState.isValidPin) null else "La tarjeta necesita un pin"
    }


    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            formCreditCardViewModel.onFieldsChanged(
                FormCreditCard(
                    alias = binding.inputAliasText.text.toString(),
                    money = binding.inputMoneyText.text.toString(),
                    pin = binding.inputPinText.text.toString()
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_credit_card)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_formCreditCardFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}