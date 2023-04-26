package com.bluemeth.simbank.src.ui.drawer.contacts.add_contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.models.utils.NotificationType
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsRVAdapter
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsViewModel
import com.bluemeth.simbank.src.ui.drawer.contacts.view_contact_profile.ViewContactProfileViewModel
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddContactFragment : Fragment() {

    private lateinit var binding: FragmentAddContactBinding

    //    private val adapter: ContactsRVAdapter = ContactsRVAdapter()
    private val addContactViewModel: AddContactViewModel by viewModels()
    private val contactsViewModel: ContactsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val viewContactProfileViewModel: ViewContactProfileViewModel by activityViewModels()

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
        setTextViews()
        setContactsRecyclerView()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.inputSearchText.addTextChangedListener { searchText ->
            filterRecyclerView(searchText)
        }
    }

    private fun initObservers() {
        addContactViewModel.navigateToHome.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("Se ha enviado la solicitud")
                missionDoned()
                goToHome()
            }
        }

        contactsViewModel.contactBlocked.observe(requireActivity()) { contactDeleted ->
            observeContacts()
            setTextViews()

            globalViewModel.getUserFromDB().observe(requireActivity()) { currentUser ->
                val snackbar =
                    Snackbar.make(requireView(), "Acción realizada", Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer") {
                    addContactViewModel.undoBlockContact(currentUser, contactDeleted)
                    toast("Acción desecha")
                }

                snackbar.show()
            }
        }

        addContactViewModel.contactUpdated.observe(requireActivity()) {
            observeContacts()
            setTextViews()
        }

        addContactViewModel.notificationDeleted.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("Se ha cancelado la solicitud")
                addContactViewModel.adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvContacts
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = addContactViewModel.adapter

        addContactViewModel.adapter.addContact = true

        addContactViewModel.adapter.setItemListener(object :
            ContactsRVAdapter.OnItemsClickListener {
            override fun onViewProfileClick(user: User) {
                viewContactProfileViewModel.setContact(user)
                goToViewProfileContact()
            }

            override fun onActionClick(user: User) {
                log("hoool", "frag")
                showSendRequestQuestionDialog(user)
            }

            override fun onCancelRequestClick(user: User) {
                Log.i("hoool", "fragment")
                showCancelRequestQuestionDialog(user)
            }

            override fun onBlockClick(user: User) {
                showBlockContactQuestionDialog(user)
            }

        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        addContactViewModel.getUsersFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                globalViewModel.getUserFromDB().observe(requireActivity()) {
                    Methods.setTimeout(
                        {
                            binding.sflContacts.isVisible = false
                            binding.rvContacts.isVisible = true

                            val newContactList = mutableListOf<User>()
                            for (contact in contactsList) {
                                var addContact = true
                                for (blockedContact in it.blocked_contacts) {
                                    if (blockedContact == contact.email) {
                                        addContact = false
                                    }
                                }

                                for (friend in it.contacts) {
                                    if (friend == contact.email) {
                                        addContact = false
                                    }
                                }
                                if (addContact) newContactList.add(contact)
                            }

                            addContactViewModel.adapter.setListData(newContactList)
                            addContactViewModel.adapter.notifyDataSetChanged()
                            setTextViews()
                        }, 300
                    )
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterRecyclerView(searchText: Editable?) {
        addContactViewModel.getUsersFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                globalViewModel.getUserFromDB().observe(requireActivity()) {

                    val newContactList = mutableListOf<User>()

                    for (contact in contactsList) {
                        var addContact = true
                        for (blockedContact in it.blocked_contacts) {
                            if (blockedContact == contact.email) {
                                addContact = false
                            }
                        }

                        for (friend in it.contacts) {
                            if (friend == contact.email) {
                                addContact = false
                            }
                        }
                        if (addContact) newContactList.add(contact)
                    }

                    val filteredList =
                        newContactList.filter { user ->
                            user.name.lowercase()
                                .contains(searchText.toString().lowercase())
                        }

                    addContactViewModel.adapter.setListData(filteredList as MutableList<User>)
                    addContactViewModel.adapter.notifyDataSetChanged()
                    setTextViews()
                }

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

    private fun blockContact(blockedContact: User) {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            contactsViewModel.addUserBlockedContactToDB(it, blockedContact)
        }
    }

    private fun cancelContactRequest(blockedContact: User) {
        addContactViewModel.getContactRequestFromDB(
            globalViewModel.getUserAuth().email!!,
            blockedContact.email
        ).observe(requireActivity()) {
            addContactViewModel.deleteNotificationFromDB(it.id)

        }
    }

    private fun setTextViews() {
        binding.tvTotalContacts.text = addContactViewModel.adapter.getListData.size.toString()
    }

    private fun showBlockContactQuestionDialog(blockedContact: User) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = "¿Seguro que quieres bloquear a este contacto?",
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    "No te aparecerá este contacto",
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action("Bloquear") {
                blockContact(blockedContact)
                it.dismiss()

            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showCancelRequestQuestionDialog(contact: User) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = "¿Seguro que quieres cancelar la solicitud de contacto?",
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    "El contacto no verá tu solicitud",
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.back)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action("Cancelar") {
                cancelContactRequest(contact)
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun showSendRequestQuestionDialog(newContact: User) {
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

    private fun goToViewProfileContact() {
        val bundle = bundleOf("friend" to false)
        view?.findNavController()
            ?.navigate(R.id.action_addContactFragment_to_viewContactProfileFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)

        tvTitle.text = getString(R.string.toolbar_add_contact)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_addContactFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }
}