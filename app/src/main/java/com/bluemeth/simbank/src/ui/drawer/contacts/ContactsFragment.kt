package com.bluemeth.simbank.src.ui.drawer.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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
import com.bluemeth.simbank.databinding.FragmentContactsBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.drawer.contacts.view_contact_profile.ViewContactProfileViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.utils.Methods
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
//    private val addapter: ContactsRVAdapter = ContactsRVAdapter()
    private val contactsViewModel: ContactsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val viewContactProfileViewModel: ViewContactProfileViewModel by activityViewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
        setContactsRecyclerView()
        setTextViews()
    }

    private fun initListeners() {
        binding.llAddContact.setOnClickListener { goToAddContact() }
        binding.llSeeBlockedContacts.setOnClickListener { goToBlockedContacts() }
        binding.inputSearchText.addTextChangedListener { searchText ->
            filterRecyclerView(searchText)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        contactsViewModel.contactDeleted.observe(requireActivity()) { contactDeleted ->
            observeContacts()
            setTextViews()

            globalViewModel.getUserFromDB().observe(requireActivity()) { currentUser ->
                val snackbar =
                    Snackbar.make(requireView(), "Acción realizada", Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer") {
                    contactsViewModel.undoDeleteContact(currentUser, contactDeleted)
                    toast("Acción desecha")
                }

                snackbar.show()
            }

        }

        contactsViewModel.contactUpdated.observe(requireActivity()) {
            observeContacts()
            setTextViews()
        }

        contactsViewModel.contactBlocked.observe(requireActivity()) { contactDeleted ->
            observeContacts()
            setTextViews()

            globalViewModel.getUserFromDB().observe(requireActivity()) { currentUser ->
                val snackbar =
                    Snackbar.make(requireView(), "Acción realizada", Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer") {
                    contactsViewModel.undoBlockContact(currentUser, contactDeleted)
                    toast("Acción desecha")
                }

                snackbar.show()
            }
        }
    }


    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvContacts
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = contactsViewModel.adapter

        contactsViewModel.adapter.setItemListener(object :
            ContactsRVAdapter.OnItemsClickListener {
            override fun onViewProfileClick(user: User) {
                viewContactProfileViewModel.setContact(user)
                goToViewProfileContact()
            }

            override fun onActionClick(user: User) {
                showDeleteContactQuestionDialog(user)
            }

            override fun onCancelRequestClick(user: User) {}

            override fun onBlockClick(user: User) {
                showBlockContactQuestionDialog(user)
            }

        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        binding.sflContacts.isVisible = true
        binding.rvContacts.isVisible = false
        contactsViewModel.getUserContactsFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                contactsViewModel.adapter.setListData(contactsList)
                contactsViewModel.adapter.notifyDataSetChanged()

                Methods.setTimeout(
                    {
                        binding.sflContacts.isVisible = false
                        binding.rvContacts.isVisible = true
                        setTextViews()
                    }, 300
                )

            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterRecyclerView(searchText: Editable?) {
        contactsViewModel.getUserContactsFromDB2(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->

                val listFiltered =
                    contactsList.filter { user ->
                        user.name.lowercase()
                            .contains(searchText.toString().lowercase())
                    }

                contactsViewModel.adapter.setListData(listFiltered as MutableList<User>)
                contactsViewModel.adapter.notifyDataSetChanged()
                setTextViews()
            }
    }

    private fun deleteContact(deleteContact: User) {
        contactsViewModel.deleteUserContactFromDB(
            globalViewModel.getUserAuth().email!!,
            deleteContact
        )
    }

    private fun blockContact(blockedContact: User) {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            contactsViewModel.addUserBlockedContactToDB(it, blockedContact)
        }
    }

    private fun showDeleteContactQuestionDialog(deleteContact: User) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = getString(R.string.dialog_remove_contact_body),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    getString(R.string.dialog_send_noti_help),
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.remove)) {
                deleteContact(deleteContact)
                it.dismiss()

            }
        ).show(dialogLauncher, requireActivity())
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

    private fun goToBlockedContacts() {
        view?.findNavController()?.navigate(R.id.action_contactsFragment_to_blockedContactsFragment)
    }

    private fun setTextViews() {
        binding.tvTotalContacts.text = contactsViewModel.adapter.getListData.size.toString()
    }

    private fun goToAddContact() {
        view?.findNavController()?.navigate(R.id.action_contactsFragment_to_addContactFragment)
    }

    private fun goToViewProfileContact() {
        val bundle = bundleOf("friend" to true)
        view?.findNavController()?.navigate(R.id.action_contactsFragment_to_viewContactProfileFragment, bundle)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_contacts)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_contactsFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }
}