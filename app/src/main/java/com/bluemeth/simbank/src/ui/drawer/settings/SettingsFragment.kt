package com.bluemeth.simbank.src.ui.drawer.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentPrivacyPolicyBinding
import com.bluemeth.simbank.databinding.FragmentSettingsBinding
import com.bluemeth.simbank.src.data.providers.PromotionsHeaderProvider
import com.bluemeth.simbank.src.data.providers.SettingsProvider
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.PromotionsHeaderRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsHeader

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentSettingsBinding.inflate(inflater,container,false)

        initUI()
        return binding.root
    }

    private fun initUI(){
        setRecyclerView()
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.rvSettings
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)

        val cardAdapter = SettingsRVAdapter()
        cardRecyclerview.adapter = cardAdapter
        cardAdapter.setListData(SettingsProvider.getListSettings())

    }




}