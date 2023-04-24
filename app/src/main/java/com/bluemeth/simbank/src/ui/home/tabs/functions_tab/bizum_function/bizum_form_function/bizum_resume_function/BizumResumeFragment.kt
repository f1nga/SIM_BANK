package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumResumeBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.data.models.*
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import com.squareup.picasso.Picasso
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.btnConfirm.setOnClickListener { makeBizum() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeBizum() {
        globalViewModel.getBankAccountFromDB()
            .observe(requireActivity()) { bankAccount ->
                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                    bizumFormViewModel.movement?.let { movement ->
                        globalViewModel.getBankAccountFromDBbyIban(movement.beneficiary_iban)
                            .observe(requireActivity()) { beneficiaryAccount ->
                                createBizum(bankAccount, movement, beneficiaryAccount, user)
                            }
                    }

                    bizumFormViewModel.bizumFormMdel?.let { bizumFormModel ->
                        globalViewModel.getBankAccountFromDBbyPhone(bizumFormModel.addresse!!.phoneNumber)
                            .observe(requireActivity()) { beneficiaryAccount ->
                                if (arguments?.getString("form_type") == "Enviar dinero") {
                                    sendBizum(bizumFormModel, beneficiaryAccount, bankAccount, user)
                                } else {
                                    requestBizum(
                                        bizumFormModel,
                                        beneficiaryAccount,
                                        bankAccount,
                                        user
                                    )
                                }
                                sendNotification()
                            }
                    }
                }
            }
    }

    private fun sendBizum(
        bizumFormModel: BizumFormModel,
        beneficiaryAccount: BankAccount,
        bankAccount: BankAccount,
        user: User
    ) {
        val movement = Movement(
            beneficiary_iban = beneficiaryAccount.iban,
            beneficiary_name = bizumFormModel.addresse!!.name,
            amount = bizumFormModel.addresse!!.import,
            subject = bizumFormModel.subject,
            category = "Pagos Bizum",
            payment_type = PaymentType.Bizum,
            remaining_money = Methods.roundOffDecimal(
                bankAccount.money - bizumFormModel.addresse!!.import
            ),
            beneficiary_remaining_money = Methods.roundOffDecimal(
                beneficiaryAccount.money + bizumFormModel.addresse!!.import
            ),
            user_email = user.email
        )

        createBizum(bankAccount, movement, beneficiaryAccount, user)

    }

    private fun requestBizum(
        bizumFormModel: BizumFormModel,
        beneficiaryAccount: BankAccount,
        bankAccount: BankAccount,
        user: User
    ) {
        val requestedBizum = RequestedBizum(
            beneficiary_iban = bankAccount.iban,
            beneficiary_name = user.name,
            amount = bizumFormModel.addresse!!.import,
            subject = bizumFormModel.subject,
            user_email = beneficiaryAccount.user_email
        )
        bizumResumeViewModel.requestBizum(
            Notification(
                title = getString(R.string.noti_bizum_request_title),
                description = getString(R.string.noti_bizum_request_description) + " ${user.name}",
                type = NotificationType.BizumRequested,
                movementID = requestedBizum.id,
                user_send_email = user.email,
                user_receive_email = beneficiaryAccount.user_email
            ),
            requestedBizum
        )
        missionDoned(Constants.REQUEST_BIZUM_MISSION)
    }

    private fun createBizum(
        bankAccount: BankAccount,
        movement: Movement,
        beneficiaryAccount: BankAccount,
        user: User
    ) {
        bizumResumeViewModel.makeBizum(
            bankAccount.iban,
            movement = movement,
            beneficiaryAccount.money,
            beneficiaryAccount.iban,
            Notification(
                title = getString(R.string.noti_bizum_received_title),
                description = getString(R.string.noti_bizum_received_description) + " ${user.name}",
                type = NotificationType.BizumReceived,
                movementID = movement.id,
                user_send_email = user.email,
                user_receive_email = beneficiaryAccount.user_email
            )
        )
        missionDoned(Constants.SEND_BIZUM_MISSION)
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
                clearArguments()
            }
        }

        bizumResumeViewModel.showErrorDialog.observe(requireActivity()) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun missionDoned(mission: Mission) {
        globalViewModel.setUserMissionToDB(mission)
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        bizumFormViewModel.bizumFormMdel?.let {
            binding.tvTotalImport.text = it.import
            binding.tvImportEveryAddresse.text = Methods.formatMoney(it.addresse!!.import)
            binding.tvComision.text = "0,00€"
            if (it.subject != "") {
                binding.tvTitleSubject.isVisible = true
                binding.tvSubject.isVisible = true
                binding.tvSubject.text = it.subject
            }
            binding.tvPhoneNumber.text = it.addresse!!.phoneNumber.toString()
            binding.tvContactName.text = it.addresse!!.name
            globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                Picasso.get().load(user.image).into(binding.ivCircle)
            }
        }

        bizumFormViewModel.movement?.let {
            binding.tvTotalImport.text = Methods.formatMoney(it.amount)
            binding.tvImportEveryAddresse.text = Methods.formatMoney(it.amount)
            binding.tvComision.text = "0,00€"
            if (it.subject != "") {
                binding.tvTitleSubject.isVisible = true
                binding.tvSubject.isVisible = true
                binding.tvSubject.text = it.subject
            }
            binding.tvContactName.text = it.beneficiary_name
            globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                binding.tvPhoneNumber.text = user.phone.toString()
                Picasso.get().load(user.image).into(binding.ivCircle)
            }
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
            binding.tvAccount.text = "Cuenta *${Methods.formatShortIban(it)}"
        }

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

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_bizum_resume)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_bizumResumeFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }

}