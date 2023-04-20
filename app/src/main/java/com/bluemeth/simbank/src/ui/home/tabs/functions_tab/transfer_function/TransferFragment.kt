package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentTransferBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.models.TransferFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.resume_transfer_function.ResumeTransferViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferFragment : Fragment() {

    private lateinit var binding: FragmentTransferBinding
    private val transferHistoryRVAdapter: TransferHistoryRVAdapter = TransferHistoryRVAdapter()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val resumeTransferViewModel: ResumeTransferViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setTransfersRecyclerView()
        setTextViews()
        initListeners()
    }

    private fun initListeners() {
        binding.llReUse.setOnClickListener { changeHistorialVisibility() }
        binding.llNewDestiny.setOnClickListener { goToTransferForm() }
        binding.llContacts.setOnClickListener { goToAddContactFromAgenda() }
    }

    private fun setTransfersRecyclerView() {

        val movementRecyclerView = binding.rvTransferHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = transferHistoryRVAdapter

        transferHistoryRVAdapter.setItemListener(object :
            TransferHistoryRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                reUseTransfer(movement)
            }
        })

        observeTransfers()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeTransfers() {
        globalViewModel.getSendedMovementsFromDB().observe(requireActivity()) {
            for (movement in it) {
                if (movement.payment_type == PaymentType.Transfer)
                    transferHistoryRVAdapter.setMovement(movement)
            }
            transferHistoryRVAdapter.notifyDataSetChanged()
        }
    }

    private fun reUseTransfer(movement: Movement) {
        globalViewModel.getBankAccountFromDBbyIban(movement.beneficiary_iban)
            .observe(requireActivity()) {

                resumeTransferViewModel.setTransferFormModel(
                    TransferFormModel(
                        iban = movement.beneficiary_iban,
                        beneficiary = movement.beneficiary_name,
                        import = movement.amount.toString(),
                        subject = movement.subject,
                    )
                )

                goToResumeTransfer()
            }

    }


    private fun changeHistorialVisibility() {
        binding.llLastTransfers.isVisible = !binding.llLastTransfers.isVisible
        binding.rvTransferHistory.isVisible = !binding.rvTransferHistory.isVisible

        if (binding.llLastTransfers.isVisible) {
            binding.ivExpand.setImageResource(R.drawable.ic_minus)
            binding.cvReuse.setCardBackgroundColor(Color.parseColor("#C8BFBDBD"))

        } else {
            binding.ivExpand.setImageResource(R.drawable.ic_plus)
            binding.cvReuse.setCardBackgroundColor(Color.parseColor("#F7189EDC"))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        globalViewModel.getBankAccountFromDB().observe(requireActivity()) {
            binding.tvAccount.text = "Cuenta *${Methods.formatShortIban(it.iban)}"
            binding.tvShortNumber.text = "· ${Methods.formatShortIban(it.iban)}"
            binding.tvMoneyAccount.text = Methods.formatMoney(it.money)

        }
    }

    private fun goToTransferForm() {
        view?.findNavController()?.navigate(R.id.action_transferFragment_to_transferFormFragment)
    }

    private fun goToAddContactFromAgenda() {
        val bundle = bundleOf("coming_from" to arguments?.getString("transfer_form"))

        view?.findNavController()?.navigate(R.id.action_transferFragment_to_addContactFromAgendaFragment, bundle)
    }

    private fun goToResumeTransfer() {
        view?.findNavController()?.navigate(R.id.action_transferFragment_to_resumeTransferFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)

        tvTitle.text = getString(R.string.toolbar_transfer)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_transferFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }

}