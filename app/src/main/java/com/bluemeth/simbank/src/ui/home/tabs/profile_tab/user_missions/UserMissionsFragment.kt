package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.user_missions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentMissionsBinding
import com.bluemeth.simbank.databinding.FragmentProfileBinding
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.CreditCardRVAdapter
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserMissionsFragment : Fragment() {
    private val adapter : UserMissionRVAdapter = UserMissionRVAdapter()
    private lateinit var binding: FragmentMissionsBinding
    private val userMissionViewModel: UserMissionViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMissionsBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI(){
    setRecyclerView()
    }


    private fun setRecyclerView() {

        val cardRecyclerview = binding.rvMissions
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = adapter
        adapter.setItemListener(object: UserMissionRVAdapter.OnItemClickListener {

            override fun onItemClick(mission: Mission) {

            }
        })

        observeCard()
    }

    private fun observeCard() {
        globalViewModel.getUserFromDB().observe(requireActivity()){
            userMissionViewModel.getMissionsFromDB(it.missions_completed).observe(requireActivity()){ missionList ->
                adapter.setListData(missionList)
                adapter.notifyDataSetChanged()
            }
        }

    }


}