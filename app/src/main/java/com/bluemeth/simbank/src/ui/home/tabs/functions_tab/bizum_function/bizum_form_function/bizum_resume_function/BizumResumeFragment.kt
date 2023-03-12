package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumResumeBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bizum_address_item.*
import kotlinx.android.synthetic.main.fragment_bizum_resume.*
import kotlinx.android.synthetic.main.fragment_resum_transfer.*
import javax.inject.Inject


@AndroidEntryPoint
class BizumResumeFragment : Fragment() {

    private lateinit var binding: FragmentBizumResumeBinding
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val bizumResumeViewModel: BizumResumeViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumResumeBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        setTextViews()
        initListeners()
        initObservers()
        setAgendaRecyclerView()
        observeAgenda()
        onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.btnConfirm.setOnClickListener {
            bizumFormViewModel.bizumFormModel!!.apply {

                this.addressesList!!.forEach { contactBizum ->
                    globalViewModel.getBankAccountFromDBbyPhone(contactBizum.phoneNumber)
                        .observe(requireActivity()) { beneficiaryAccount ->
                    globalViewModel.getBankAccountFromDB()
                        .observe(requireActivity()) { bankAccount ->
                                    bizumResumeViewModel.makeBizum(
                                        bankAccount.iban,
                                        Movement(
                                            beneficiary_iban = beneficiaryAccount.iban,
                                            beneficiary_name = contactBizum.name,
                                            amount = contactBizum.import,
                                            subject = this.subject,
                                            payment_type = PaymentType.Bizum,
                                            remaining_money = Methods.roundOffDecimal(bankAccount.money - contactBizum.import),
                                            user_email = globalViewModel.getUserAuth().email!!
                                        ),
                                        beneficiaryAccount.money,
                                        beneficiaryAccount.iban
                                    )
                            Methods.sendNotification("SIMBANK","Has enviado un bizum de ${tvTotalImport.text}",requireContext())
                                }

                        }
                }
            }
        }
    }


    private fun initObservers() {
        bizumResumeViewModel.navigateToVerifyEmail.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                showSuccessDialog()
            }
        }

        bizumResumeViewModel.showErrorDialog.observe(requireActivity()) { showError ->
            if (showError) showErrorDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        bizumFormViewModel.bizumFormModel!!.apply {
            binding.tvTotalImport.text = this.import
            binding.tvImportEveryAddresse.text = Methods.formatMoney(this.addressesList!![0].import)
            binding.tvComision.text = "0,00€"
            if (this.subject != "") {
                binding.tvTitleSubject.isVisible = true
                binding.tvSubject.isVisible = true
                binding.tvSubject.text = this.subject
            }
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
            binding.tvAccount.text = "Cuenta *${Methods.formatShortIban(it)}"
        }

    }

    private fun setAgendaRecyclerView() {
        val addressesRecycler = binding.rvAddressesResum
        addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        addressesRecycler.setHasFixedSize(true)
        addressesRecycler.adapter = bizumResumeViewModel.agendaRVAdapter

        bizumResumeViewModel.agendaRVAdapter.showCheckBox = false

        bizumResumeViewModel.agendaRVAdapter.setItemListener(object :
            AgendaRVAdapter.OnItemClickListener {
            override fun onItemClick(contactAgenda: ContactAgenda) {

            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeAgenda() {
        bizumFormViewModel.bizumFormModel!!.addressesList!!.forEach {
            bizumResumeViewModel.agendaRVAdapter.addToListData(
                ContactAgenda(
                    name = it.name,
                    phoneNumber = it.phoneNumber
                )
            )
        }
        bizumResumeViewModel.agendaRVAdapter.notifyDataSetChanged()
    }

    private fun onBackPressed() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    showQuestionDialog()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    private fun showQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_oops),
            description = getString(R.string.dialog_bizum_sure),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(getString(R.string.dialog_bizum_help_text), Toast.LENGTH_LONG)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                it.dismiss()
                view?.findNavController()!!
                    .navigate(R.id.action_bizumResumeFragment_to_bizumFragment)
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = "No se ha podido realizar el bizum",
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showSuccessDialog() {
        SuccessDialog.create(
            "Bizum realizado",
            "Sigue navegando por SimBank",
            SuccessDialog.Action(getString(R.string.dialog_verified_positive)) { goToHome() }
        ).show(dialogLauncher, requireActivity())
    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_bizumResumeFragment_to_homeFragment)
    }

}