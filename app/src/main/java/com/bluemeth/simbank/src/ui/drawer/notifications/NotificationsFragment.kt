package com.bluemeth.simbank.src.ui.drawer.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentNotificationsBinding
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.notifications.contact_request_detail.ContactRequestDetailViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_details.BizumDetailViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private val adapter: NotificationsRVAdapter = NotificationsRVAdapter()
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val contactRequestDetailViewModel: ContactRequestDetailViewModel by activityViewModels()
    private val movementsDetailViewModel: MovementsDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        setContactsRecyclerView()
    }

    private fun initListeners() {
    }


    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvNotifications
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = adapter

        adapter.setItemListener(object :
            NotificationsRVAdapter.OnItemClickListener {
            override fun onItemClick(notification: Notification) {
                when (notification.type) {
                    NotificationType.BizumReceived -> {
                        movementsDetailViewModel.setMovement(notification.movement!!)
                        goToBizumDetail()
                    }
                    NotificationType.TransferReceived -> {
                        movementsDetailViewModel.setMovement(notification.movement!!)
                        goToTransferDetail()
                    }
                    else -> {
                        contactRequestDetailViewModel.setNotification(notification)
                        goToContactRequestDetai()
                    }
                }
            }
        })

        observeContacts()
    }

    private fun goToContactRequestDetai() {
        view?.findNavController()
            ?.navigate(R.id.action_notificationsFragment_to_contactRequestDetailFragment)
    }

    private fun goToBizumDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_notificationsFragment_to_bizumDetailAccountFragment)
    }

    private fun goToTransferDetail() {
        view?.findNavController()
            ?.navigate(R.id.action_notificationsFragment_to_transferDetailAccountFragment)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        notificationsViewModel.getNotificationsByEmailFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { notificationsList ->
                adapter.setListData(notificationsList)
                adapter.notifyDataSetChanged()
            }
    }
}