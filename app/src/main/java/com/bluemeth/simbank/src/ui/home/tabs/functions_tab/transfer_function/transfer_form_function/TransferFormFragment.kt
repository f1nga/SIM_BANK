package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFormTransferBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.model.TransferFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.resume_transfer_function.ResumeTransferViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferFormFragment : Fragment() {

    private lateinit var binding: FragmentFormTransferBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val transferFormViewModel: TransferFormViewModel by viewModels()
    private val resumeTransferViewModel: ResumeTransferViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormTransferBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setTextViews()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            inputIbantext.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputIbantext.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputIbantext.onTextChanged { onFieldChanged() }

            inputBeneficiaryText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputBeneficiaryText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputBeneficiaryText.onTextChanged { onFieldChanged() }

            inputImportText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputImportText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputImportText.setOnFocusChangeListener { _, hasFocus -> formatInputImport(hasFocus) }
            inputImportText.onTextChanged { onFieldChanged() }

            btnContinue.setOnClickListener {
                it.dismissKeyboard()
                val newImport = if (inputImportText.text.toString().contains("€")) {
                    Methods.splitEuro(inputImportText.text.toString())
                } else inputImportText.text.toString()

                transferFormViewModel.onContinueSelected(
                    TransferFormModel(
                        iban = inputIbantext.text.toString(),
                        beneficiary = inputBeneficiaryText.text.toString(),
                        import = newImport
                    )
                )
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            transferFormViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        transferFormViewModel.navigateToTransferResum.observe(requireActivity()) {
            with(binding) {
                val newImport = if (inputImportText.text.toString().contains("€")) {
                    Methods.splitEuro(inputImportText.text.toString())
                } else inputImportText.text.toString()

                if (inputImportText.text!!.isNotEmpty()) {
                    resumeTransferViewModel.setTransfer(
                        Movement(
                            beneficiary_iban = inputIbantext.text.toString(),
                            beneficiary_name = inputBeneficiaryText.text.toString(),
                            amount = newImport.toDouble(),
                            category = "Transferencias",
                            isIncome = false,
                            payment_type = PaymentType.Transfer,
                            subject = if (inputAsuntoText.text!!.isNotEmpty()) inputAsuntoText.text.toString() else "",
                            user_email = globalViewModel.getUserAuth().email!!
                        )
                    )

                }
            }
            goToTransferResum()
        }
    }

    private fun formatInputImport(hasFocus: Boolean) {
        val inputText = Editable.Factory.getInstance()

        binding.inputImportText.apply {
            if (text.toString().isNotEmpty()) {
                text = if (hasFocus) {
                    inputText.newEditable(Methods.splitEuro(text.toString()))
                } else {
                    inputText.newEditable(
                        Methods.formatMoney(
                            Methods.roundOffDecimal(
                                text.toString().toDouble()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun updateUI(viewState: TransferFormViewState) {
        binding.inputIban.error =
            if (!viewState.isValidIbanSpaces) "No debe contener espacios"
            else {
                if (viewState.isValidIban) null else "El IBAN no es válido"
            }
        binding.inputBeneficiary.error =
            if (viewState.isValidBeneficiary) null else "Debe contener al menos 6 caracteres"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {

        //autoSpacesIban()

        if (!hasFocus) {
            val newImport = binding.inputImportText.text.toString().let {
                if (it.contains("€")) {
                    Methods.splitEuro(it)
                } else it
            }


            transferFormViewModel.onNameFieldsChanged(
                TransferFormModel(
                    iban = binding.inputIbantext.text.toString(),
                    beneficiary = binding.inputBeneficiaryText.text.toString(),
                    import = newImport
                )
            )
        }
    }

    private fun autoSpacesIban() {
        with(binding.inputIbantext) {
            val inputText = Editable.Factory.getInstance()

            if (text!!.length == 4) {
                text = inputText.newEditable("$text ")
                setSelection(text!!.length)
            } else {
                if ((text!!.length % 5 == 0 && text!!.isNotEmpty())) {

                    text = inputText.newEditable("$text ")
                    setSelection(text!!.length)
                }
            }
        }
    }

    private fun setTextViews() {
        with(binding) {
            globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
                tvAccount.text = "Cuenta *${Methods.formatShortIban(it.iban)}"
                tvShortNumber.text = "· ${Methods.formatShortIban(it.iban)}"
                tvMoneyAccount.text = Methods.formatMoney(it.money)
            }

            if (resumeTransferViewModel.movement != null) {
                val transfer = resumeTransferViewModel.movement!!
                val inputText = Editable.Factory.getInstance()

                inputIbantext.text = inputText.newEditable(transfer.beneficiary_iban)
                inputBeneficiaryText.text = inputText.newEditable(transfer.beneficiary_name)
                inputImportText.text = inputText.newEditable(transfer.amount.toString())
                inputAsuntoText.text = inputText.newEditable(transfer.subject)
            }
        }
    }

    private fun goToTransferResum() {
        view?.findNavController()?.navigate(R.id.action_transferFormFragment_to_resumeTransferFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_transfer)
    }

}