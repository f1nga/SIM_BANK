package com.bluemeth.simbank.src.ui.drawer.contacts.add_contact

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsRVAdapter
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddContactFragment : Fragment() {

    private lateinit var binding: FragmentAddContactBinding
    private val adapter: ContactsRVAdapter = ContactsRVAdapter()
    private val addContactViewModel: AddContactViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setContactsRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        addContactViewModel.navigateToHome.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("Se ha enviado la solicitud")
                missionDoned()
                goToHome()
            }
        }
    }

    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvContacts
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = adapter

        adapter.addContact = true

        adapter.setItemListener(object :
            ContactsRVAdapter.OnItemsClickListener {
            override fun onViewProfileClick(user: User) {

            }

            override fun onActionClick(user: User) {
                showQuestionDialog(user)

            }

            override fun onBlockClick(user: User) {
            }

        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        addContactViewModel.getUsersFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                adapter.setListData(contactsList)
                adapter.notifyDataSetChanged()

                Methods.setTimeout(
                    {
                        binding.sflContacts.isVisible = false
                        binding.rvContacts.isVisible = true
                    }, 300
                )
            }
    }

    private fun sendNotification(user: User, newContact: User) {
        addContactViewModel.insertNotificationToDB(
            Notification(
                title = getString(R.string.noti_contact_request_title),
                description = getString(R.string.noti_contact_request_description) + " ${user.name}",
                type = NotificationType.ContactRequest,
                user_send_email = user.email,
                user_receive_email = newContact.email
            )
        )
    }

    private fun missionDoned() {
        globalViewModel.setUserMissionToDB(Constants.ADD_CONTACT_MISSION)
    }

    private fun showQuestionDialog(newContact: User) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = getString(R.string.dialog_send_noti_body),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    getString(R.string.dialog_send_noti_help),
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.send)) {
                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                    sendNotification(user, newContact)
                    it.dismiss()
                }
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_addContactFragment_to_homeFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)

        tvTitle.text = getString(R.string.toolbar_add_contact)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_addContactFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}