package com.bluemeth.simbank.src.ui.drawer.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentContactsBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private val adapter: ContactsRVAdapter = ContactsRVAdapter()
    private val contactsViewModel: ContactsViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

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
    }

    private fun initListeners() {
        binding.llAddContact.setOnClickListener { goToAddContact() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        contactsViewModel.contactDeleted.observe(requireActivity()) {
            toast("El contacto se ha eliminado de tu lista")
            adapter.deleteContact(it)
            adapter.notifyDataSetChanged()
        }
    }


    private fun setContactsRecyclerView() {
        val movementRecyclerView = binding.rvContacts
        movementRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        movementRecyclerView.setHasFixedSize(true)
        movementRecyclerView.adapter = adapter

        adapter.setItemListener(object :
            ContactsRVAdapter.OnItemsClickListener {
            override fun onViewProfileClick(user: User) {
                toast("viewprofile")
            }

            override fun onActionClick(user: User) {
                showQuestionDialog(user)
            }

            override fun onBlockClick(user: User) {
                toast("block")

            }

        })

        observeContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeContacts() {
        contactsViewModel.getUserContactsFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { contactsList ->
                log("hoolcontactsFragment", contactsList.toString())
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

    private fun deleteContact(deleteContact: User) {
        contactsViewModel.deleteUserContactFromDB(
            globalViewModel.getUserAuth().email!!,
            deleteContact
        )
    }

    private fun showQuestionDialog(deleteContact: User) {
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

    private fun goToAddContact() {
        view?.findNavController()?.navigate(R.id.action_contactsFragment_to_addContactFragment)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_contacts)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_contactsFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}