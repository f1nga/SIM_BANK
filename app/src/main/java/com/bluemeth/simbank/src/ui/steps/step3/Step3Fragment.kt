package com.bluemeth.simbank.src.ui.steps.step3

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
import com.bluemeth.simbank.databinding.FragmentStep3Binding
import com.bluemeth.simbank.src.core.ex.*
import com.bluemeth.simbank.src.ui.steps.step3.model.Step3Model
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step3Fragment : Fragment() {
    private lateinit var binding: FragmentStep3Binding
    private val step3ViewModel: Step3ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStep3Binding.inflate(inflater, container, false)

        initUI()
       // generateNumberCreditCard()
        return binding.root
    }


    private fun initUI() {
        viewGone()
        changeVisibility()
        initListeners()
        initObservers()
    }


    private fun initListeners() {

        binding.inputAliasText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputAliasText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputAliasText.onTextChanged { onFieldChanged() }

        binding.inputPinText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputPinText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputPinText.onTextChanged { onFieldChanged() }


        binding.btnGenerate.setOnClickListener {
            step3ViewModel.onGeneratedButtonClicked(
                Step3Model(
                    alias = binding.inputAliasText.text.toString(),
                    pin = binding.inputPinText.text.toString()
                )
            )

            generateNumberCreditCard()
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
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            step3ViewModel.onFieldsChanged(
                Step3Model(
                    binding.inputAliasText.text.toString(),
                    binding.inputPinText.text.toString()
                )
            )
        }
    }

    fun generateNumberCreditCard() {
        binding.btnGenerate.setOnClickListener() {
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }
            binding.tvNumeroTarjeta.text = Methods.formatCardNumber(creditCardNumber)
        }
    }


    fun viewGone() {
        binding.tvNumeroTarjeta.visibility = View.GONE
        binding.btnGenerate.visibility = View.GONE
        binding.inputAlias.visibility = View.GONE
        binding.inputPin.visibility = View.GONE
    }

    fun changeVisibility() {

        binding.tvGenerarNumero.setOnClickListener() {
            if (binding.tvNumeroTarjeta.visibility == 0) {
                binding.tvNumeroTarjeta.visibility = View.GONE
                binding.btnGenerate.visibility = View.GONE
            } else {
                binding.tvNumeroTarjeta.visibility = View.VISIBLE
                binding.btnGenerate.visibility = View.VISIBLE
            }
        }

        binding.tvAliasCreditCard.setOnClickListener() {
            if (binding.inputAlias.visibility == 0) {
                binding.inputAlias.visibility = View.GONE
            } else {
                binding.inputAlias.visibility = View.VISIBLE
            }
        }

        binding.tvPin.setOnClickListener() {
            if (binding.inputPin.visibility == 0) {
                binding.inputPin.visibility = View.GONE
            } else {
                binding.inputPin.visibility = View.VISIBLE
            }
        }

        binding.imageView2.setOnClickListener() {
            if (binding.tvNumeroTarjeta.visibility == 0) {
                binding.tvNumeroTarjeta.visibility = View.GONE
                binding.btnGenerate.visibility = View.GONE
            } else {
                binding.tvNumeroTarjeta.visibility = View.VISIBLE
                binding.btnGenerate.visibility = View.VISIBLE
            }
        }

        binding.imageView3.setOnClickListener() {
            if (binding.inputAlias.visibility == 0) {
                binding.inputAlias.visibility = View.GONE
            } else {
                binding.inputAlias.visibility = View.VISIBLE
            }
        }


        binding.imageView4.setOnClickListener() {
            if (binding.inputPin.visibility == 0) {
                binding.inputPin.visibility = View.GONE
            } else {
                binding.inputPin.visibility = View.VISIBLE
            }
        }
    }
}