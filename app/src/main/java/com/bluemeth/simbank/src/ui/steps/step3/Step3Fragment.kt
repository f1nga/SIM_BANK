package com.bluemeth.simbank.src.ui.steps.step3

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentStep3Binding
import com.bluemeth.simbank.src.core.ex.*
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.ui.steps.step3.model.Step3Model
import com.bluemeth.simbank.src.ui.steps.step4.Step4ViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step3Fragment : Fragment() {
    private lateinit var binding: FragmentStep3Binding
    private val step3ViewModel: Step3ViewModel by viewModels()
    private val step4ViewModel: Step4ViewModel by activityViewModels()
    private var numberCard: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStep3Binding.inflate(inflater, container, false)

        initUI()
        return binding.root
    }

    private fun initUI() {
        changeVisibility()
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

            btnGenerate.setOnClickListener {
                btnGenerate.isVisible = false
                lottieLoading.isVisible = true

                Handler(Looper.getMainLooper()).postDelayed({
                    lottieLoading.isVisible = false
                    btnGenerate.isVisible = true

                    numberCard = Methods.generateLongNumber(16)
                    binding.tvNumeroTarjeta.text =  Methods.formatCardNumber(numberCard!!)
                }, 2100)

                step3ViewModel.onFieldsChanged(
                    Step3Model(
                        alias = inputAliasText.text.toString(),
                        pin = inputPinText.text.toString(),
                        isGeneratedNumberButtonClicked = true
                    )
                )
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            step3ViewModel.viewState.collect { viewState ->
                updateUI(viewState)

                val btnNext = requireActivity().findViewById<Button>(R.id.nextButton)
                btnNext.isVisible = viewState.isStep3Validated()

            }
        }
    }

    private fun updateUI(viewState: Step3ViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)

        binding.inputPin.error =
            if (viewState.isValidPin) null else getString(R.string.pin_not_correct)

        binding.btnGenerate.error =
            if (viewState.isButtonClicked) null else "Genera el número"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        with(binding) {
            if (inputAliasText.text.toString().isNotEmpty() && inputPinText.text.toString()
                    .isNotEmpty()
            ) {
                step4ViewModel.setNewCreditCard(
                    CreditCard(
                        number = numberCard ?: "",
                        alias = inputAliasText.text.toString(),
                        money = step4ViewModel.newBankAccount.money,
                        pin = inputPinText.text.toString().toInt(),
                        cvv = 924,
                        caducity = Methods.generateCaducityCard(),
                        type = CreditCardType.Debito,
                        bank_iban = step4ViewModel.newBankAccount.iban
                    )
                )
            }

            if (!hasFocus) {
                step3ViewModel.onFieldsChanged(
                    Step3Model(
                        alias = inputAliasText.text.toString(),
                        pin = inputPinText.text.toString(),
                        isGeneratedNumberButtonClicked = tvNumeroTarjeta.text.toString() != "0000 0000 0000 0000" || !btnGenerate.isVisible
                    )
                )
            }
        }

    }

    private fun changeVisibility() {

        with(binding) {
            tvGenerarNumero.setOnClickListener {
                btnGenerate.isVisible = !btnGenerate.isVisible
                tvNumeroTarjeta.isVisible = !tvNumeroTarjeta.isVisible
            }

            tvAliasCreditCard.setOnClickListener { inputAlias.isVisible = !inputAlias.isVisible }

            tvPin.setOnClickListener { inputPin.isVisible = !inputPin.isVisible }

            imageView2.setOnClickListener {
                btnGenerate.isVisible = !btnGenerate.isVisible
                tvNumeroTarjeta.isVisible = !tvNumeroTarjeta.isVisible
            }

            imageView3.setOnClickListener { inputAlias.isVisible = !inputAlias.isVisible }

            imageView4.setOnClickListener { inputPin.isVisible = !inputPin.isVisible }
        }
    }
}