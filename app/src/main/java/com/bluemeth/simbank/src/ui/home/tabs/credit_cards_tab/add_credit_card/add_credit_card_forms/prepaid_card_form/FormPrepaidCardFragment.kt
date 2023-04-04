package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form

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
import com.bluemeth.simbank.databinding.FragmentFormPrepaidCardBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form.model.FormPrepaidCard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormPrepaidCardFragment : Fragment() {

    private lateinit var binding: FragmentFormPrepaidCardBinding
    private val formPrepaidCardViewModel: FormPrepaidCardViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormPrepaidCardBinding.inflate(inflater, container, false)


        initUI()
        return binding.root
    }

    fun initUI() {
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

            inputMoneyText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputMoneyText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputMoneyText.onTextChanged { onFieldChanged() }


            btnSolicitarPrepaid.setOnClickListener() {
                globalViewModel.getBankIban().observe(requireActivity()) {
                    formPrepaidCardViewModel.onFinishSelected(
                        requireContext(), it, FormPrepaidCard(
                            alias = inputAliasText.text.toString(),
                            pin = inputPinText.text.toString(),
                            money = inputMoneyText.text.toString()

                        )
                    )
                }
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            formPrepaidCardViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
        formPrepaidCardViewModel.navigateToCards.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_formPrepaidCardFragment_to_cardFragment)
                toast("¡Muy bien! Acabas de solicitar tu nueva tarjeta!")
            }
        }
    }

    private fun updateUI(viewState: FormPrepaidCardViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)
        binding.inputPin.error =
            if (viewState.isValidPin) null else "Debe contener 4 caracteres"
        binding.inputMoney.error =
            if (viewState.isValidMoney) null else "No deberias usar más de 10.000€ euros"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            formPrepaidCardViewModel.onFieldsChanged(
                FormPrepaidCard(
                    alias = binding.inputAliasText.text.toString(),
                    pin = binding.inputPinText.text.toString(),
                    money = binding.inputMoneyText.text.toString()
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_prepaid_card)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_formPrepaidCardFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}