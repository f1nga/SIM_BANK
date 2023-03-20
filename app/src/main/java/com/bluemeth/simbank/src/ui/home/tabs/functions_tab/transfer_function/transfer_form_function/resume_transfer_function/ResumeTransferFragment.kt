package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.resume_transfer_function

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentResumTransferBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResumeTransferFragment : Fragment() {

    private lateinit var binding: FragmentResumTransferBinding
    private val resumeTransferViewModel: ResumeTransferViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumTransferBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        setInfoAccount()
        setTextViews()
        initListeners()
        initObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        with(binding) {
            tvModifyDestiny.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_resumeTransferFragment_to_transferFragment)
            }
            tvModifyImport.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_resumeTransferFragment_to_transferFragment)
            }
            tvModifySubject.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_resumeTransferFragment_to_transferFragment)
            }

            btnFinish.setOnClickListener {
                globalViewModel.getBankAccountFromDB().observe(requireActivity()) { bankAccount ->
                    val transfer = resumeTransferViewModel.movement!!
                    globalViewModel.getBankAccountFromDBbyIban(transfer.beneficiary_iban)
                        .observe(requireActivity()) { beneficiaryBankAccount ->
                            resumeTransferViewModel.insertTransferToDB(
                                bankAccount.iban,
                                Movement(
                                    beneficiary_iban = transfer.beneficiary_iban,
                                    beneficiary_name = transfer.beneficiary_name,
                                    amount = transfer.amount,
                                    subject = transfer.subject,
                                    category = transfer.category,
                                    payment_type = PaymentType.Transfer,
                                    remaining_money = Methods.roundOffDecimal(bankAccount.money - transfer.amount),
                                    beneficiary_remaining_money = Methods.roundOffDecimal(
                                        beneficiaryBankAccount.money + transfer.amount
                                    ),
                                    user_email = globalViewModel.getUserAuth().email!!
                                ),
                                beneficiaryBankAccount.money,
                                beneficiaryBankAccount.iban
                            )
                            Methods.sendNotification(
                                "SIMBANK",
                                "Has realizado una transferencia",
                                requireContext()
                            )
                        }
                }
            }

        }
    }

    private fun initObservers() {
        resumeTransferViewModel.showErrorDialog.observe(requireActivity()) { showDialog ->
            if (showDialog) showErrorDialog()
        }

        resumeTransferViewModel.navigateToHome.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                showSuccessDialog()
            }
        }
    }

    private fun setTextViews() {
        with(binding) {
            resumeTransferViewModel.movement?.let {
                tvCapitals.text = Methods.splitBeneficiaryName(it.beneficiary_name)
                tvBeneficiary.text = it.beneficiary_name
                tvIban.text = Methods.formatIbanNumber(it.beneficiary_iban)
                tvMoney.text = Methods.formatMoney(it.amount)
                tvSubject.text = it.subject
            }

            resumeTransferViewModel.reUseTransferArguments?.let {
                tvIban.text = it.iban
                tvBeneficiary.text = it.beneficiary
                tvCapitals.text = Methods.splitNameAndSurname(it.beneficiary)
                tvMoney.text = Methods.formatMoney(Methods.roundOffDecimal(it.import.toDouble()))
                tvSubject.text = it.subject
            }
        }
    }

    private fun setInfoAccount() {
        globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
            binding.tvAccount.text = "Cuenta *${Methods.formatShortIban(it.iban)}"
            binding.tvShortNumber.text = "· ${Methods.formatShortIban(it.iban)}"
            binding.tvMoneyAccount.text = Methods.formatMoney(it.money)
        }
    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_resumeTransferFragment_to_homeFragment)
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = "No se ha podido realizar la transferencia",
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showSuccessDialog() {
        SuccessDialog.create(
            "Transferencia realizada",
            "Sigue navegando por SimBank",
            SuccessDialog.Action(getString(R.string.dialog_verified_positive)) { goToHome() }
        ).show(dialogLauncher, requireActivity())
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_transfer_resume)
    }
}