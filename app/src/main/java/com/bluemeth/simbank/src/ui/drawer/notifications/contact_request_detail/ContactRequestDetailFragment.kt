package com.bluemeth.simbank.src.ui.drawer.notifications.contact_request_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentContactRequestDetailBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactRequestDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactRequestDetailBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val contactRequestDetailViewModel: ContactRequestDetailViewModel by activityViewModels()
    private lateinit var notification: Notification

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactRequestDetailBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        notification = contactRequestDetailViewModel.notification
        initListeners()
        initObservers()
        setTextViews()
    }

    private fun initObservers() {
        contactRequestDetailViewModel.contactUpdated.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("El contacto ha sido agregado a tu lista")
                goToNotifications()
            }
        }

        contactRequestDetailViewModel.declineRequest.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("Has rechazado la solicitud")
                goToNotifications()
            }
        }
    }

    private fun initListeners() {
        binding.btnAcceptRequest.setOnClickListener { acceptRequest() }
        binding.btnDeclineRequest.setOnClickListener { showQuestionDialog() }
    }

    private fun acceptRequest() {
        globalViewModel.getUserByEmail(notification.user_receive_email)
            .observe(requireActivity()) { userReceive ->
                notification.user_send_email?.let {
                    globalViewModel.getUserByEmail(it).observe(requireActivity()) { userSend ->
                        contactRequestDetailViewModel.addNewUserContactToDB(
                            userReceive,
                            userSend
                        )
                    }
                }
            }
    }

    private fun goToNotifications() {
        view?.findNavController()
            ?.navigate(R.id.action_contactRequestDetailFragment_to_notificationsFragment)
    }

    private fun showQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = getString(R.string.dialog_decline_request_body),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    getString(R.string.dialog_decline_request_help),
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.declinar)) {
                contactRequestDetailViewModel.declineContactRequest()
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun setTextViews() {
        globalViewModel.getUserByEmail(notification.user_send_email!!)
            .observe(requireActivity()) { userReceive ->
                Picasso.get().load(userReceive.image).into(binding.ivCircle)
                binding.tvNameSendNoti.text = userReceive.name
                binding.tvPhoneSendNoti.text = userReceive.phone.toString()
            }
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_contact_request)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_contactRequestDetailFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}