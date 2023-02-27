package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.HomeHeaderProvider
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeTabViewModel: HomeTabViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)

        setMoneyTextViews()

        setHeaderRecyclerView()
        observeHeader()

        setMovementRecyclerView()
        observeMovement()

        setDrawerHeaderName()

        return binding.root
    }

    private fun setHeaderRecyclerView() {

        val headerRecyclerView = binding.rvHeader
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        headerRecyclerView.setHasFixedSize(true)
        headerRecyclerView.adapter = homeViewModel.headerAdapter

        homeViewModel.headerAdapter.setItemListener(object : HorizontalListRVAdapter.onItemClickListener {
            override fun onItemClick(creditCard: HomeHeader) {
                toast("Feature not implemented")
            }
        })
    }

    private fun observeHeader() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            homeViewModel.headerAdapter.setListData(HomeHeaderProvider.getListHeader(it))
            homeViewModel.headerAdapter.notifyDataSetChanged()
        }
    }

    private fun setMovementRecyclerView() {

        val movementRecyclerView = binding.rvHistory
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = homeViewModel.movementAdapter

        homeViewModel.movementAdapter.setItemListener(object : MovementRecordsRVAdapter.onItemClickListener {
            override fun onItemClick(creditCard: Movement) {
                toast("HOLHOL")
            }
        })

        globalViewModel.getBankMoney().observe(requireActivity()) {
            homeViewModel.movementAdapter.setRemainingMoney(it)
        }
    }

    private fun observeMovement() {
        homeTabViewModel.getMovementsFromDB("ES33 4920 4829 0293 3849 3810").observe(requireActivity()) {
            homeViewModel.movementAdapter.setListData(it)
            homeViewModel.movementAdapter.notifyDataSetChanged()
        }
    }

    private fun setMoneyTextViews() {
        globalViewModel.getBankMoney().observe(requireActivity()) {
            binding.tvDineroCuenta.text = Methods.formatMoney(it)
            binding.tvMoneyTotal.text = Methods.formatMoney(it)
        }
    }

    private fun setDrawerHeaderName() {
        globalViewModel.getUserName().observe(requireActivity()) {
            activity?.findViewById<TextView>(R.id.tvNameDrawer)?.text = Methods.splitName(it)
        }
    }

    override fun onStart() {
        super.onStart()
        globalViewModel.getUserName().observe(this) {
            val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView
            tvTitle.text = "Hola, ${Methods.splitName(it)}"
        }
    }
}