package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.resume_transfer_function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumTransferBinding.inflate(inflater, container, false)

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
                globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
                    val transfer = resumeTransferViewModel.movement!!

                    resumeTransferViewModel.insertTransferToDB(
                        it.iban,
                        Movement(
                            beneficiary_iban = transfer.beneficiary_iban,
                            beneficiary_name = transfer.beneficiary_name,
                            amount = transfer.amount,
                            subject = transfer.subject,
                            isIncome = false,
                            payment_type = PaymentType.Transfer,
                            remaining_money = Methods.roundOffDecimal(it.money - transfer.amount) ,
                            user_email = globalViewModel.getUserAuth().email!!
                        )
                    )
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

    private fun setTextViews() {
        with(binding) {
            globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
                tvAccount.text = "Cuenta *${Methods.formatShortIban(it.iban)}"
                tvShortNumber.text = "· ${Methods.formatShortIban(it.iban)}"
                tvMoneyAccount.text = Methods.formatMoney(it.money)
            }

            val transfer = resumeTransferViewModel.movement!!
            tvCapitals.text = Methods.splitBeneficiaryName(transfer.beneficiary_name)
            tvBeneficiary.text = transfer.beneficiary_name
            tvIban.text = Methods.formatIbanNumber(transfer.beneficiary_iban)
            tvMoney.text = Methods.formatMoney(transfer.amount)
            tvSubject.text = transfer.subject

        }

    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_resumeTransferFragment_to_homeFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_transfer_resume)
    }
}