package com.bluemeth.simbank.src.ui.drawer.contacts.blocked_contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBlockedContactsBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BlockedContactsFragment : Fragment() {

    private lateinit var binding: FragmentBlockedContactsBinding
    private val adapter: BlockedContactsRVAdapter = BlockedContactsRVAdapter()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val blockedContactsViewModel: BlockedContactsViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlockedContactsBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setTextViews()
        initObservers()
        initListeners()
        setContactsRecyclerView()
    }

    private fun initListeners() {
        binding.inputSearchText.addTextChangedListener { searchText ->
            filterRecyclerView(searchText)
        }
    }

    private fun initObservers() {
        blockedContactsViewModel.contactDesblocked.observe(requireActivity()) { desblockedContact ->
            observeContacts()
            setTextViews()

            globalViewModel.getUserFromDB().observe(requireActivity()) { currentUser ->
                val snackbar =
                    Snackbar.make(requireView(), "Acción realizada", Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer") {
                    blockedContactsViewModel.undoDesblockContact(currentUser, desblockedContact)
                    toast("Acción desecha")
                }

                snackbar.show()
            }
        }

        blockedContactsViewModel.contactBlocked.observe(requireActivity()) {
            observeContacts()
            setTextViews()
        }
    }


    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvBlockedContacts
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = adapter

        adapter.setItemListener(object :
            BlockedContactsRVAdapter.OnItemsClickListener {
            override fun onClick(user: User) {
                showDesblockContactQuestionDialog(user)
            }

        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        globalViewModel.getUsersFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                globalViewModel.getUserFromDB().observe(requireActivity()) {
                    Methods.setTimeout(
                        {
                            binding.sflBlockedContacts.isVisible = false
                            binding.rvBlockedContacts.isVisible = true

                            val newContactList = mutableListOf<User>()
                            for (contact in contactsList) {
                                var addContact = false
                                for (blockedContact in it.blocked_contacts) {
                                    if (blockedContact == contact.email) {
                                        addContact = true
                                    }
                                }
                                if (addContact) newContactList.add(contact)
                            }

                            adapter.setListData(newContactList)
                            adapter.notifyDataSetChanged()
                            setTextViews()
                        }, 300
                    )
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterRecyclerView(searchText: Editable?) {
        globalViewModel.getUsersFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                globalViewModel.getUserFromDB().observe(requireActivity()) {

                    val newContactList = mutableListOf<User>()
                    for (contact in contactsList) {
                        var addContact = false
                        for (blockedContact in it.blocked_contacts) {
                            if (blockedContact == contact.email) {
                                addContact = true
                            }
                        }
                        if (addContact) newContactList.add(contact)
                    }

                    val filteredList =
                        newContactList.filter { user ->
                            user.name.lowercase()
                                .contains(searchText.toString().lowercase())
                        }

                    adapter.setListData(filteredList as MutableList<User>)
                    adapter.notifyDataSetChanged()
                    setTextViews()

                }
            }
    }

    private fun setTextViews() {
        binding.tvTotalContacts.text = adapter.getListData.size.toString()
    }

    private fun desblockContact(user: User) {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            blockedContactsViewModel.desblockUserContactToDB(it, user)
        }
    }

    private fun showDesblockContactQuestionDialog(blockedContact: User) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_careful),
            description = "¿Seguro que quieres desbloquear a este contacto?",
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(
                    "Este contacto volverá a aparecer en la lista",
                    Toast.LENGTH_LONG
                )
            },
            negativeAction = QuestionDialog.Action(getString(R.string.cancel)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action("Desbloquear") {
                desblockContact(blockedContact)
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_blocked_contacts)

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