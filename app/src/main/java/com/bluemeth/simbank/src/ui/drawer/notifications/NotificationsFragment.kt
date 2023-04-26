package com.bluemeth.simbank.src.ui.drawer.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentNotificationsBinding
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.notifications.contact_request_detail.ContactRequestDetailViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.account_movements_details.MovementsDetailsViewModel
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private val adapter: NotificationsRVAdapter = NotificationsRVAdapter()
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val contactRequestDetailViewModel: ContactRequestDetailViewModel by activityViewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
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
        initObservers()
        setContactsRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        notificationsViewModel.notificationUpdated.observe(requireActivity()) {
            adapter.removeNotification(it)
            it.readed = !it.readed
            adapter.setNotification(it)
            adapter.notifyDataSetChanged()

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    val ivNoti = requireActivity().findViewById<ImageView>(R.id.ivNotifications)
                    ivNoti.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }

        notificationsViewModel.notificationDeleted.observe(requireActivity()) {
            adapter.removeNotification(it)
            adapter.notifyDataSetChanged()
        }
    }


    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvNotifications
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = adapter

        adapter.setItemListener(object :
            NotificationsRVAdapter.OnItemClickListener {
            override fun onItemClick(notification: Notification) {
                goToNotificationDetail(notification)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDeleteClick(notification: Notification) {
                deleteNotification(notification)
            }

            override fun onMarkAsReadedClick(notification: Notification) {
                updateReadedNotification(notification, !notification.readed)
            }
        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        notificationsViewModel.getNotificationsByEmailFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { notificationsList ->
                adapter.setListData(notificationsList)
                adapter.notifyDataSetChanged()
            }
    }

    private fun goToNotificationDetail(notification: Notification) {
        updateReadedNotification(notification, true)

        log("tyyype", notification.type.toString())

        when (notification.type) {
            NotificationType.BizumReceived -> {
                notificationsViewModel.getMovementByIdFromDB(notification.movementID!!)
                    .observe(requireActivity()) { movement ->
                        movementsDetailViewModel.setMovement(movement)
                        goToBizumDetail()
                    }

            }
            NotificationType.TransferReceived -> {
                notificationsViewModel.getMovementByIdFromDB(notification.movementID!!)
                    .observe(requireActivity()) { movement ->
                        movementsDetailViewModel.setMovement(movement)
                        goToTransferDetail()
                    }
            }
            NotificationType.BizumRequested -> {
                notificationsViewModel.getRequestedBizumByIdFromDB(notification.movementID!!)
                    .observe(requireActivity()) { requestedBizum ->
                        globalViewModel.getBankAccountFromDBbyIban(requestedBizum.beneficiary_iban)
                            .observe(requireActivity()) { beneficiaryBankAccount ->
                                globalViewModel.getBankAccountFromDB()
                                    .observe(requireActivity()) { ordenanteBankAccount ->
                                        bizumFormViewModel.setMovement(
                                            Movement(
                                                id = requestedBizum.id,
                                                beneficiary_iban = requestedBizum.beneficiary_iban,
                                                beneficiary_name = requestedBizum.beneficiary_name,
                                                amount = requestedBizum.amount,
                                                subject = requestedBizum.subject,
                                                category = "Pagos Bizum",
                                                payment_type = PaymentType.Bizum,
                                                remaining_money = Methods.roundOffDecimal(
                                                    ordenanteBankAccount.money - requestedBizum.amount
                                                ),
                                                beneficiary_remaining_money =  Methods.roundOffDecimal(
                                                    beneficiaryBankAccount.money + requestedBizum.amount
                                                ),
                                                user_email = globalViewModel.getUserAuth().email!!
                                            )
                                        )
                                        goToBizumResume()
                                    }
                            }

                    }
            }
            else -> {
                contactRequestDetailViewModel.setNotification(notification)
                goToContactRequestDetai()
            }
        }
    }

    private fun deleteNotification(notification: Notification) {
        notificationsViewModel.deleteNotificationFromDB(notification)
    }

    private fun updateReadedNotification(notification: Notification, readed: Boolean) {
        notificationsViewModel.updateReadedNotificationFromDB(notification, readed)
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

    private fun goToBizumResume() {
        val bundle = bundleOf(Constants.FORM_TYPE to Constants.REQUEST_MONEY)

        view?.findNavController()
            ?.navigate(R.id.action_notificationsFragment_to_bizumResumeFragment, bundle)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_notifications)
    }
}