package com.bluemeth.simbank.src.ui.steps.step2

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
import com.bluemeth.simbank.databinding.FragmentStep2Binding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.steps.step2.model.Step2Model
import com.bluemeth.simbank.src.ui.steps.step4.Step4ViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step2Fragment : Fragment() {
    private lateinit var binding: FragmentStep2Binding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val step2ViewModel: Step2ViewModel by viewModels()
    private val step4ViewModel: Step4ViewModel by activityViewModels()
    private var bankIban: String? = null
    private var moneyBank: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStep2Binding.inflate(inflater, container, false)

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

            generateIban.setOnClickListener {
                generateIban.isVisible = false
                lottieLoading.visibility = View.VISIBLE
                tvIban.isVisible = false

                Handler(Looper.getMainLooper()).postDelayed({
                    lottieLoading.visibility = View.GONE
                    generateIban.isVisible = true

                    bankIban = "ES33" + Methods.generateLongNumber(20)
                    binding.tvIban.text = Methods.formatIbanNumber(bankIban!!)
                    tvIban.isVisible = true
                }, 2100)

                step2ViewModel.onFieldsChanged(
                    Step2Model(
                        alias = inputAliasText.text.toString(),
                        isGeneratedIBANButtonClicked = true,
                        tvDinero.text.toString() != "0" || !buttonGenerateMoney.isVisible
                    )
                )
            }

            buttonGenerateMoney.setOnClickListener {
                viewPlaceholder.visibility = View.VISIBLE
                buttonGenerateMoney.visibility = View.GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    viewPlaceholder.isVisible = false
                    binding.buttonGenerateMoney.isVisible = false

                    moneyBank = Methods.roundOffDecimal(Methods.generateMoneyBank())
                    binding.tvDinero.text = Methods.formatMoney(moneyBank!!)
                    binding.tvDinero.isVisible = true
                }, 2700)

                step2ViewModel.onFieldsChanged(
                    Step2Model(
                        alias = inputAliasText.text.toString(),
                        isGeneratedIBANButtonClicked = binding.tvIban.text.toString() != "0" || !generateIban.isVisible,
                        isGeneratedMoneyButtonClicked = true
                    )
                )
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            step2ViewModel.viewState.collect { viewState ->
                updateUI(viewState)

                val btnNext = requireActivity().findViewById<Button>(R.id.nextButton)
                btnNext.isVisible = viewState.isStep2Validated()
            }
        }
    }

    private fun updateUI(viewState: Step2ViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)
        binding.buttonGenerateMoney.error =
            if (viewState.isButtonMoneyClicked) null else "Debes generar tu dinero"
        binding.generateIban.error =
            if (viewState.isButtonIbanClicked) null else "Debes generar un IBAN"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (binding.inputAliasText.text.toString().isNotEmpty()) {
                step4ViewModel.setNewBankAccount(
                    BankAccount(
                        iban = bankIban ?: "",
                        alias = binding.inputAliasText.text.toString(),
                        money = moneyBank ?: 0.0,
                        user_email = globalViewModel.getUserAuth().email!!
                    )
                )
        }

        if (!hasFocus) {
            step2ViewModel.onFieldsChanged(
                Step2Model(
                    alias = binding.inputAliasText.text.toString(),
                    isGeneratedIBANButtonClicked = binding.tvIban.text.toString() != "0",
                    isGeneratedMoneyButtonClicked = binding.tvDinero.text.toString() != "0"
                )
            )
        }
    }

    private fun changeVisibility() {
        with(binding) {
            tvGenerarIBAN.setOnClickListener() {
                if(tvIban.text.toString() == "0") {
                    tvIban.isVisible = false
                } else {
                    tvIban.isVisible = !tvIban.isVisible
                }

                generateIban.isVisible = !generateIban.isVisible
            }

            tvGenerarDinero.setOnClickListener() {
                if (tvDinero.isVisible) {
                    buttonGenerateMoney.isVisible = false
                } else {
                    buttonGenerateMoney.isVisible = !buttonGenerateMoney.isVisible
                }
            }

            tvAlias.setOnClickListener() { inputAlias.isVisible = !inputAlias.isVisible }

            imageView2.setOnClickListener() {
                tvIban.isVisible = !tvIban.isVisible
                generateIban.isVisible = !generateIban.isVisible
            }

            imageView3.setOnClickListener() {
                if (tvDinero.isVisible) {
                    buttonGenerateMoney.isVisible = false
                } else {
                    buttonGenerateMoney.isVisible = !buttonGenerateMoney.isVisible
                }
            }

            imageView4.setOnClickListener() { inputAlias.isVisible = !inputAlias.isVisible }
        }
    }
}