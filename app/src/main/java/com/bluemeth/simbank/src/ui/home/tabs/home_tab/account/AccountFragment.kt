package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAccountBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val movementsDetailsViewModel: MovementsDetailsViewModel by activityViewModels()

    private companion object {
        const val COLOR_SELECTED = "#F7189EDC"
        const val COLOR_UNSELECTED = "#C8BFBDBD"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        initUI()
        return binding.root
    }

    private fun initUI() {
        setTextViews()
        setMovementsRecyclerView()
        initListeners()

    }

    private fun initListeners() {
        with(binding) {
            cvIngresos.setOnClickListener { filterIngresos() }
            cvPerdidas.setOnClickListener { filterPerdidas() }
            llToMovements.setOnClickListener { goToMovementsFunction() }
            llToTransfer.setOnClickListener { goToTransferFunction() }
            llToBizum.setOnClickListener { goToBizumFunction() }
            tvMoreInformation.setOnClickListener { goToInfoAccount() }
        }
    }

    private fun setMovementsRecyclerView() {

        val movementRecyclerView = binding.rvMovements
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = accountViewModel.accountMovementsAdapter

        accountViewModel.accountMovementsAdapter.setItemListener(object :
            AccountMovementsRVAdapter.OnItemClickListener {
            override fun onItemClick(movement: Movement) {
                movementsDetailsViewModel.setMovement(movement)
                when (movement.payment_type) {
                    PaymentType.Bizum -> {
                        goToBizumDetail()
                    }
                    else -> {
                        goToTransferDetail()
                    }
                }
            }
        })

        observeMovements(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeMovements(isIncome: Boolean) {
        accountViewModel.accountMovementsAdapter.clearListData()
        accountViewModel.accountMovementsAdapter.notifyDataSetChanged()

        if (isIncome) {
            globalViewModel.getReceivedMovementsFromDB()
                .observe(requireActivity()) { movementsList ->
                    accountViewModel.accountMovementsAdapter.setListData(movementsList)
                    accountViewModel.accountMovementsAdapter.notifyDataSetChanged()
                }
        } else {
            globalViewModel.getSendedMovementsFromDB()
                .observe(requireActivity()) { movementsList ->
                    accountViewModel.accountMovementsAdapter.setListData(movementsList)
                    accountViewModel.accountMovementsAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun filterPerdidas() {
        if (binding.tvIngresos.currentTextColor == Color.parseColor(COLOR_SELECTED)) {

            binding.tvIngresos.setTextColor(Color.parseColor(COLOR_UNSELECTED))
            binding.tVPerdidas.setTextColor(Color.parseColor(COLOR_SELECTED))
            observeMovements(false)
        }

    }

    private fun filterIngresos() {
        if (binding.tVPerdidas.currentTextColor == Color.parseColor(COLOR_SELECTED)) {

            binding.tVPerdidas.setTextColor(Color.parseColor(COLOR_UNSELECTED))
            binding.tvIngresos.setTextColor(Color.parseColor(COLOR_SELECTED))
            observeMovements(true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextViews() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            binding.tvDisponibleMoney.text = Methods.formatMoney(it)
            binding.tvActualMoney.text = Methods.formatMoney(it)
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
            binding.tvAccountNumber.text = "Cuenta *${Methods.formatShortIban(it)}"
            binding.tvShortNumber.text = "· ${Methods.formatShortIban(it)}"
        }
    }

    private fun goToBizumDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_accountFragment_to_bizumDetailAccountFragment)
    }

    private fun goToTransferDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_accountFragment_to_transferDetailAccountFragment)
    }

    private fun goToMovementsFunction() {
        view?.findNavController()?.navigate(R.id.action_accountFragment_to_searchMovementsFragment)
    }

    private fun goToBizumFunction() {
        view?.findNavController()?.navigate(R.id.action_accountFragment_to_bizumFragment)
    }

    private fun goToTransferFunction() {
        view?.findNavController()?.navigate(R.id.action_accountFragment_to_transferFragment)
    }

    private fun goToInfoAccount() {
        view?.findNavController()?.navigate(R.id.action_accountFragment_to_infoAccountFragment)
    }


    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_account)
    }
}