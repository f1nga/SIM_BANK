package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFormDebidCardBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form.model.FormDebitCard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormDebidCardFragment : Fragment() {
    private lateinit var binding: FragmentFormDebidCardBinding
    private val formDebitCardViewModel: FormDebitCardViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormDebidCardBinding.inflate(inflater, container, false)

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

            btnSolicitarDebit.setOnClickListener() {
                globalViewModel.getBankIban().observe(requireActivity()){
                    formDebitCardViewModel.onFinishSelected(requireContext(),it,FormDebitCard(
                        alias = inputAliasText.text.toString(),
                        pin = inputPinText.text.toString()
                    ))
                }
            }
        }

    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            formDebitCardViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
        formDebitCardViewModel.navigateToCards.observe(requireActivity()){
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_formDebidCardFragment_to_creditCardFragment)
                toast("¡Muy bien! Acabas de solicitar tu nueva tarjeta!")
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
                    pin = binding.inputPinText.text.toString(),
                )
            )
        }
    }


}