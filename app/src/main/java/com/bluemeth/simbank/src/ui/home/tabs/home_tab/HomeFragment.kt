package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentHomeBinding
import com.bluemeth.simbank.src.SimBankApp
import com.bluemeth.simbank.src.core.ex.setActivityTitle
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.RecyclerClickListener
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private var listData: List<HomeHeader>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)

        setRecyclerView()
        observeCard()

        return binding.root
    }

    private fun setRecyclerView() {

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

    fun fillHeaderList() {
        homeViewModel.getBankAccount().observe(requireActivity()) {
            homeViewModel.headerList.value = listOf(
                HomeHeader(it.money, "Ver ahorros"),
                HomeHeader(it.money, "Ver gastos")
            )
        }
    }

    private fun observeCard() {
        homeViewModel.setListData()
        homeViewModel.headerList.observe(requireActivity()) {
            homeViewModel.headerAdapter.setListData(it)

        }
    }
}