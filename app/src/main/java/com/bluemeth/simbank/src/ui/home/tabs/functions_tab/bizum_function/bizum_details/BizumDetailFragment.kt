package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumDetailBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BizumDetailFragment : Fragment() {

    private lateinit var binding: FragmentBizumDetailBinding
    private lateinit var movement: Movement
    private val bizumDetailViewModel: BizumDetailViewModel by activityViewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumDetailBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        movement = bizumDetailViewModel.movement
        setTextViews()
        initListeners()
    }

    private fun initListeners() {
        binding.llReUseSend.setOnClickListener { reUseBizum("Enviar dinero") }
        binding.llReUseSolicitud.setOnClickListener { reUseBizum("Solicitar dinero") }
    }

    private fun reUseBizum(formType: String) {
        globalViewModel.getBankAccountFromDBbyIban(movement.beneficiary_iban)
            .observe(requireActivity()) {
                globalViewModel.getUserByEmail(it.user_email)
                    .observe(requireActivity()) { beneficiary ->
                        bizumFormViewModel.setReUseBizumArguments(
                            BizumFormModel(
                                import = movement.amount.toString(),
                                subject = movement.subject,
                                addresse = ContactBizum(
                                    name = movement.beneficiary_name,
                                    import = movement.amount,
                                    phoneNumber = beneficiary.phone

                                )

                            )
                        )

                        goToBizumForm(bundleOf("form_type" to formType))
                    }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        with(binding) {
            tvDate.text = Methods.formatLongDate(movement.date.toDate())

            if (movement.isIncome) {
                globalViewModel.getUserByEmail(movement.user_email).observe(requireActivity()) {
                    tvTitle.text = "Recibido de ${Methods.splitNameAndCapitalsSurnames(it.name)}"
                }
                tvSubject.text =
                    if (movement.subject != "") "Recibido: ${movement.subject}" else "Recibido: sin concepto"
                tvPrice.text = "+${Methods.formatMoney(movement.amount)}"
                ivArrowIncome.setImageResource(R.drawable.arrow_win)
                tvAccount.text = "Cuenta destino"

                globalViewModel.getUserByEmail(movement.user_email).observe(requireActivity()) {
                    tvContactName.text = it.name
                    tvPhoneNumber.text = it.phone.toString()
                    tvCapitals.text = it.name[0].toString()
                }

            } else {
                "Enviado a ${Methods.splitNameAndCapitalsSurnames(movement.beneficiary_name)}".also {
                    tvTitle.text = it
                }

                tvSubject.text =
                    if (movement.subject != "") "Enviado: ${movement.subject}" else "Enviado: sin concepto"
                tvPrice.text = "-${Methods.formatMoney(movement.amount)}"
                ivArrowIncome.setImageResource(R.drawable.arrow_lose)
                tvAccount.text = "Cuenta ordenante"

                globalViewModel.getUserByName(movement.beneficiary_name)
                    .observe(requireActivity()) {
                        tvContactName.text = it.name
                        tvPhoneNumber.text = it.phone.toString()
                        tvCapitals.text = it.name[0].toString()
                    }
            }

            globalViewModel.getBankIban().observe(requireActivity()) {
                tvAccountNumber.text = "ES****${Methods.formatShortIban(it)}"
            }

        }

    }

    private fun goToBizumForm(bundle: Bundle) {
        view?.findNavController()
            ?.navigate(R.id.action_bizumDetailFragment_to_bizumFormFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_bizum_detail)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_bizumDetailFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }

}