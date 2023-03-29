package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.btnConfirm.setOnClickListener {
            sendBizum()
//                val beneficiaryIbans = mutableListOf<String>()
//                val beneficiaryNames = mutableListOf<String>()
//                val beneficiaryMoneys = mutableListOf<Double>()
//                val beneficiaryRemainingMoneys = mutableListOf<Double>()
//
//                for (bizumForm in bizumFormMdel.addressesList!!) {
//                    beneficiaryNames.add(bizumForm.name)
//
//                }
//
//                log("hool0", beneficiaryNames.toString())
//
//                bizumResumeViewModel.getBeneficiarysAccount(beneficiaryNames)
//                    .observe(requireActivity()) { listBeneficiariesAccounts ->
//                        log("hool1", listBeneficiariesAccounts[0].user_email)
//                        for (account in listBeneficiariesAccounts) {
//                            log("hool2", account.toString())
//                            beneficiaryIbans.add(account.iban)
//                            beneficiaryMoneys.add(account.money)
//                            beneficiaryRemainingMoneys.add(
//                                Methods.roundOffDecimal(
//                                    account.money + bizumFormMdel.addressesList!![0].import
//                                )
//                            )
//                        }
//                        log("hool3", "hol")
//
//                        globalViewModel.getBankAccountFromDB()
//                            .observe(requireActivity()) { bankAccount ->
//                                log("hool4", bankAccount.user_email)
//                                bizumResumeViewModel.makeBizum(
//                                    bankAccount.iban,
//                                    Bizum(
//                                        beneficiary_iban = beneficiaryIbans,
//                                        beneficiary_name = beneficiaryNames,
//                                        amount = bizumFormMdel.addressesList!![0].import,
//                                        subject = bizumFormMdel.subject,
//                                        category = "Pagos Bizum",
//                                        payment_type = PaymentType.Bizum,
//                                        remaining_money = Methods.roundOffDecimal(bankAccount.money - bizumFormMdel.addressesList!![0].import),
//                                        beneficiary_remaining_money = beneficiaryRemainingMoneys,
//                                        user_email = globalViewModel.getUserAuth().email!!
//                                    ),
//                                    beneficiaryMoneys,
//                                )
//                            }
//                    }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendBizum() {
        bizumFormViewModel.bizumFormMdel?.let { bizumFormModel ->
            bizumFormModel.addressesList!!.forEach { contactBizum ->
                globalViewModel.getBankAccountFromDBbyPhone(contactBizum.phoneNumber)
                    .observe(requireActivity()) { beneficiaryAccount ->
                        globalViewModel.getBankAccountFromDB()
                            .observe(requireActivity()) { bankAccount ->
                                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                                    bizumResumeViewModel.makeBizum(
                                        bankAccount.iban,
                                        beneficiaryAccount.money,
                                        beneficiaryAccount.iban,
                                        Notification(
                                            title = getString(R.string.noti_bizum_received_title),
                                            description = getString(R.string.noti_bizum_received_description) + " ${user.name}",
                                            type = NotificationType.BizumReceived,
                                            movement = Movement(
                                                beneficiary_iban = beneficiaryAccount.iban,
                                                beneficiary_name = contactBizum.name,
                                                amount = contactBizum.import,
                                                subject = bizumFormModel.subject,
                                                category = "Pagos Bizum",
                                                payment_type = PaymentType.Bizum,
                                                remaining_money = Methods.roundOffDecimal(
                                                    bankAccount.money - contactBizum.import
                                                ),
                                                beneficiary_remaining_money = Methods.roundOffDecimal(
                                                    beneficiaryAccount.money + contactBizum.import
                                                ),
                                                user_email = user.email
                                            ),
                                            user_send_email = user.email,
                                            user_receive_email = beneficiaryAccount.user_email
                                        )
                                    )

                                    sendNotification()
                                }
                            }
                    }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification() {
        Methods.sendNotification(
            "SIMBANK",
            "Has enviado un bizum de ${binding.tvTotalImport.text}",
            requireContext()
        )
    }


    private fun initObservers() {
        bizumResumeViewModel.navigateToHome.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                showSuccessDialog()
                missionDoned()
                clearArguments()
            }
        }

        bizumResumeViewModel.showErrorDialog.observe(requireActivity()) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun missionDoned() {
        globalViewModel.setUserMissionToDB(Constants.BIZUM_MISSION)
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        bizumFormViewModel.bizumFormMdel!!.apply {
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
        bizumFormViewModel.bizumFormMdel!!.addressesList!!.forEach {
            bizumResumeViewModel.agendaRVAdapter.addToListData(
                ContactAgenda(
                    name = it.name,
                    phoneNumber = it.phoneNumber
                )
            )
        }
        bizumResumeViewModel.agendaRVAdapter.notifyDataSetChanged()
    }

    private fun clearArguments() {
        bizumFormViewModel.bizumFormMdel = null
        bizumFormViewModel.reUseBizumArgument = null
        bizumFormViewModel.bizumFormArgument = null
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