package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.HomeHeaderProvider
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)

        setMoneyTextViews()

//        setMovementRecyclerView()
//        observeMovement()

        setHeaderRecyclerView()
        observeHeader()

        return binding.root
    }

    private fun setHeaderRecyclerView() {

        val headerRecyclerView = binding.rvHeader
        headerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        headerRecyclerView.setHasFixedSize(true)
        headerRecyclerView.adapter = homeViewModel.headerAdapter

        homeViewModel.headerAdapter.setItemListener(object : RecyclerClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(),"Cabolo" , Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun observeHeader() {
        homeViewModel.money.observe(requireActivity()) {
            homeViewModel.headerAdapter.setListData(HomeHeaderProvider.getListHeader(it))
            homeViewModel.headerAdapter.notifyDataSetChanged()
        }
    }

//    private fun setMovementRecyclerView() {
//
//        val movementRecyclerView = binding.rvHistory
//        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        movementRecyclerView.setHasFixedSize(true)
//        movementRecyclerView.adapter = homeViewModel.movementAdapter
//
//        homeViewModel.movementAdapter.setItemListener(object : RecyclerClickListener {
//            override fun onItemClick(position: Int) {
//                Toast.makeText(requireContext(),"Cabolo" , Toast.LENGTH_SHORT).show()
//            }
//        })
//
//    }
//
//    private fun observeMovement() {
//
//            homeViewModel.movementAdapter.setListData(
//                listOf(
//                    Movement("Bon dia", Timestamp(Date(2022, 11, 23)) , 45.00, true) ,
//                    Movement("Bon dia", Timestamp(Date(2022, 11, 23)) , 45.00, true),
//                    Movement("Bon dia", Timestamp(Date(2022, 11, 23)) , 45.00, true)
//                )
//            )
//            homeViewModel.movementAdapter.notifyDataSetChanged()
//
//    }

    private fun setMoneyTextViews() {
        homeViewModel.money.observe(requireActivity()) {
            binding.tvDineroCuenta.text = Methods.formatMoney(it)
            binding.tvMoneyTotal.text = Methods.formatMoney(it)
        }
    }
}