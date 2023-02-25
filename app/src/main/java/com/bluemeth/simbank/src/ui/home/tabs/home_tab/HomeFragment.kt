package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.HomeHeaderProvider
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeTabViewModel: HomeTabViewModel by viewModels()

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

        return binding.root
    }

    private fun setHeaderRecyclerView() {

        val headerRecyclerView = binding.rvHeader
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        headerRecyclerView.setHasFixedSize(true)
        headerRecyclerView.adapter = homeViewModel.headerAdapter

        homeViewModel.headerAdapter.setItemListener(object : HorizontalListRVAdapter.onItemClickListener {
            override fun onItemClick(creditCard: HomeHeader) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun observeHeader() {
        homeViewModel.money.observe(requireActivity()) {
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
                TODO("Not yet implemented")
            }
        })

        homeViewModel.money.observe(requireActivity()) {
            Log.i("dinerro", it.toString())
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
        homeViewModel.money.observe(requireActivity()) {
            binding.tvDineroCuenta.text = Methods.formatMoney(it)
            binding.tvMoneyTotal.text = Methods.formatMoney(it)
        }
    }
}