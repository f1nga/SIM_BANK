package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAccountBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()

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
        initListeners()
        setMovementsRecyclerView()
    }

    private fun initListeners() {
        binding.cvIngresos.setOnClickListener {
            filterIngresos()
        }
        binding.cvPerdidas.setOnClickListener {
            filterPerdidas()
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
                toast("HOLHOL")
            }
        })

        observeMovements(true)
    }

    private fun observeMovements(isIncome: Boolean) {

        globalViewModel.getMovementsFromDB(globalViewModel.getUserAuth().email!!, isIncome)
            .observe(requireActivity()) {

                accountViewModel.accountMovementsAdapter.setListData(it)
                accountViewModel.accountMovementsAdapter.notifyDataSetChanged()
            }
    }

    private fun filterPerdidas() {
        if (binding.tVPerdidas.currentTextColor == Color.parseColor("#C8BFBDBD")) {

            binding.tVPerdidas.setTextColor(Color.parseColor("#F7189EDC"))
            binding.tvIngresos.setTextColor(Color.parseColor("#C8BFBDBD"))
        }
        observeMovements(false)
    }

    private fun filterIngresos() {
        if (binding.tvIngresos.currentTextColor == Color.parseColor("#C8BFBDBD")) {

            binding.tvIngresos.setTextColor(Color.parseColor("#F7189EDC"))
            binding.tVPerdidas.setTextColor(Color.parseColor("#C8BFBDBD"))
        }
        observeMovements(true)
    }

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

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_account)
    }
}