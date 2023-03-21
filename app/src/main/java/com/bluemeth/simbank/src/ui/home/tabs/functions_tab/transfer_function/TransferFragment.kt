package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentTransferBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.model.TransferFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.resume_transfer_function.ResumeTransferViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferFragment : Fragment() {

    private lateinit var binding: FragmentTransferBinding
    private val transferHistoryRVAdapter : TransferHistoryRVAdapter = TransferHistoryRVAdapter()
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
        initListeners()
    }

    private fun initListeners() {
        binding.llReUse.setOnClickListener { changeHistorialVisibility() }
        binding.llNewDestiny.setOnClickListener { goToTransferForm() }
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

    private fun observeTransfers() {
        globalViewModel.getSendedMovementsFromDB().observe(requireActivity()) {
            for(movement in it) {
                if(movement.payment_type == PaymentType.Transfer)
                    transferHistoryRVAdapter.setMovement(movement)
            }
            transferHistoryRVAdapter.notifyDataSetChanged()
        }
    }

    private fun reUseTransfer(movement: Movement) {
        globalViewModel.getBankAccountFromDBbyIban(movement.beneficiary_iban)
            .observe(requireActivity()) {
                globalViewModel.getUserByEmail(it.user_email)
                    .observe(requireActivity()) { beneficiary ->
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
    }


    private fun changeHistorialVisibility() {
        binding.llLastTransfers.isVisible = !binding.llLastTransfers.isVisible
        binding.rvTransferHistory.isVisible = !binding.rvTransferHistory.isVisible

        if(binding.llLastTransfers.isVisible) {
            binding.ivExpand.setImageResource(R.drawable.ic_minus)
            binding.cvReuse.setCardBackgroundColor(Color.parseColor("#C8BFBDBD"))

        } else {
            binding.ivExpand.setImageResource(R.drawable.ic_plus)
            binding.cvReuse.setCardBackgroundColor(Color.parseColor("#F7189EDC"))
        }
    }

    private fun goToTransferForm() {
        view?.findNavController()?.navigate(R.id.action_transferFragment_to_transferFormFragment)
    }

    private fun goToResumeTransfer() {
        view?.findNavController()?.navigate(R.id.action_transferFragment_to_resumeTransferFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)

        tvTitle.text = getString(R.string.toolbar_transfer)
    }

}