package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.user_missions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentUserMissionsBinding
import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserMissionsFragment : Fragment() {

    private val adapter: UserMissionsRVAdapter = UserMissionsRVAdapter()
    private lateinit var binding: FragmentUserMissionsBinding
    private val userMissionsViewModel: UserMissionsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserMissionsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setRecyclerView()
    }


    private fun setRecyclerView() {

        val cardRecyclerview = binding.rvMissions
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = adapter
        adapter.setItemListener(object : UserMissionsRVAdapter.OnItemClickListener {

            override fun onItemClick(mission: Mission) {
                when (mission.name) {
                    Constants.TRANSFER_MISSION.name -> goToTransfer()
                    Constants.REUSE_TRANSFER_MISSION.name -> goToTransfer()
                    Constants.PROMOTION_MISSION.name -> goToPromotions()
                    Constants.ADD_CONTACT_MISSION.name -> goToAddContact()

                    else -> goToBizum()
                }
            }
        })

        observeCard()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCard() {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            adapter.setListData(userMissionsViewModel.getMissions(it.missions_completed))
            adapter.notifyDataSetChanged()
        }
    }

    private fun goToTransfer() {
        view?.findNavController()?.navigate(R.id.action_userMissionsFragment_to_transferFragment)
    }

    private fun goToBizum() {
        view?.findNavController()?.navigate(R.id.action_userMissionsFragment_to_bizumFragment)
    }

    private fun goToAddContact() {
        view?.findNavController()?.navigate(R.id.action_userMissionsFragment_to_addContactFragment)
    }

    private fun goToPromotions() {
        view?.findNavController()?.navigate(R.id.action_userMissionsFragment_to_promotionsFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_missions)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_userMissionsFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}