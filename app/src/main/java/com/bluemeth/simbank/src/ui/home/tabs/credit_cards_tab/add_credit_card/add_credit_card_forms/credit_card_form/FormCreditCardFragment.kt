package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.bluemeth.simbank.databinding.FragmentFormCreditCardBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form.model.FormCreditCard
import com.bluemeth.simbank.src.utils.Methods


class FormCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentFormCreditCardBinding
    private var moneyCreditCard: Double? = null
    private val globalViewModel: GlobalViewModel by viewModels()
    private var numberCreditCard: String? = null
    private val formCreditCardViewModel: FormCreditCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFormCreditCardBinding.inflate(inflater,container,false)


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

            btnGenerateNumber.setOnClickListener {
                btnGenerateNumber.isVisible = false
                lottieLoading.visibility = View.VISIBLE
                tvNumeroTarjeta.isVisible = false

                Handler(Looper.getMainLooper()).postDelayed({
                    lottieLoading.visibility = View.GONE
                    btnGenerateNumber.isVisible = true

                    numberCreditCard = Methods.generateLongNumber(16)
                    binding.tvNumeroTarjeta.text = Methods.formatCardNumber(numberCreditCard!!)
                    tvNumeroTarjeta.isVisible = true
                }, 2100)

                formCreditCardViewModel.onFieldsChanged(
                    FormCreditCard(
                        alias = inputAliasText.text.toString(),
                        isGeneratedNumberButtonClicked = true,
                        isGeneratedMoneyButtonClicked = tvDinero.text.toString() != "0" || !buttonGenerateMoney.isVisible
                    )
                )
            }

            binding.buttonGenerateMoney.setOnClickListener {
                binding.viewPlaceholder.visibility = View.VISIBLE
                binding.buttonGenerateMoney.visibility = View.GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    viewPlaceholder.isVisible = false
                    binding.buttonGenerateMoney.isVisible = false

                    moneyCreditCard = Methods.roundOffDecimal(Methods.generateMoneyCreditCard())
                    binding.tvDinero.text = Methods.formatMoney(moneyCreditCard!!)
                    binding.tvDinero.isVisible = true
                }, 2700)
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            formCreditCardViewModel.viewState.collect { viewState ->
                updateUI(viewState)

                val solicitar = requireActivity().findViewById<Button>(R.id.btnSolicitarCredit)
                solicitar.isVisible = viewState.isFormCreditCardValidated()
            }
        }
    }

    private fun updateUI(viewState: FormCreditCardViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)
        binding.buttonGenerateMoney.error =
            if (viewState.isButtonMoneyClicked) null else "Debes generar tu dinero"
        binding.btnGenerateNumber.error =
            if (viewState.isButtonNumberClicked) null else "Debes generar un Numero de tarjeta"
    }


    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            formCreditCardViewModel.onFieldsChanged(
                FormCreditCard(
                    alias = binding.inputAliasText.text.toString(),
                    isGeneratedNumberButtonClicked = binding.tvNumeroTarjeta.text.toString() != "0",
                    isGeneratedMoneyButtonClicked = binding.tvDinero.text.toString() != "0"
                )
            )
        }
    }



}