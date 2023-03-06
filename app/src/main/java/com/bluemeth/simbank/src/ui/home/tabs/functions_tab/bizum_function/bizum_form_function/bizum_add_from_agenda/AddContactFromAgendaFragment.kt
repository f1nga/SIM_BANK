package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactFromAgendaBinding
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContactFromAgendaFragment : Fragment() {

    private lateinit var binding: FragmentAddContactFromAgendaBinding
    private val addContactFromAgendaViewModel: AddContactFromAgendaViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private var contactsAdded: MutableList<ContactBizum> = mutableListOf()
    private var contactsRemoved: MutableList<ContactBizum> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactFromAgendaBinding.inflate(inflater, container, false)

        initUI()
        return binding.root
    }

    private fun initUI() {
        setAgendaRecyclerView()
        observeAgenda()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnAccept.setOnClickListener {
            addContactFromAgendaViewModel.onAcceptSelected()
        }
    }

    private fun initObservers() {
        addContactFromAgendaViewModel.navigateToBizumForm.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
//                for (userBizum in contactsAdded) {
//                    bizumFormViewModel.addressesRVAdapter.setUserBizum(userBizum)
//                }
//                for (userBizum in contactsRemoved) {
//                    bizumFormViewModel.addressesRVAdapter.deleteUserBizum(userBizum)
//                }
                addContactFromAgendaViewModel.agendaRVAdapter.listData.forEach { contactAgenda ->
                    if(contactAgenda.isChecked) {
                        val import =
                            if (bizumFormViewModel.bizumFormArguments?.import!! != "") bizumFormViewModel.bizumFormArguments?.import!!.toDouble() else 0.0
                        bizumFormViewModel.addressesRVAdapter.setUserBizum(
                            ContactBizum(
                                name = contactAgenda.name,
                                import = import,
                                phoneNumber = contactAgenda.phoneNumber
                            )
                        )
                    } else {
                        val import =
                            if (bizumFormViewModel.bizumFormArguments?.import!! != "") bizumFormViewModel.bizumFormArguments?.import!!.toDouble() else 0.0
                        bizumFormViewModel.addressesRVAdapter.deleteUserBizum(
                            ContactBizum(
                                name = contactAgenda.name,
                                import = import,
                                phoneNumber = contactAgenda.phoneNumber
                            )
                        )
                    }
                }
                goToBizumForm()
            }
        }

    }

    private fun setAgendaRecyclerView() {
        val addressesRecycler = binding.rvAgenda
        addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        addressesRecycler.setHasFixedSize(true)
        addressesRecycler.adapter = addContactFromAgendaViewModel.agendaRVAdapter

        addContactFromAgendaViewModel.agendaRVAdapter.setItemListener(object :
            AgendaRVAdapter.OnItemClickListener {
            override fun onItemClick(contactAgenda: ContactAgenda) {
                val import =
                    if (bizumFormViewModel.bizumFormArguments?.import!! != "") bizumFormViewModel.bizumFormArguments?.import!!.toDouble() else 0.0

//                if (bizumFormViewModel.bizumFormArguments?.import!! != "")  {
//                    Methods.splitEuroDouble(bizumFormViewModel.bizumFormArguments?.import!!)
//                } else 0.0
                val contactBizum = ContactBizum(
                    name = contactAgenda.name,
                    import = import,
                    phoneNumber = contactAgenda.phoneNumber
                )

                if (!contactAgenda.isChecked) {
                    contactsAdded.add(contactBizum)
                } else {
                    contactsRemoved.add(contactBizum)
                }

//                contactAgenda.isChecked = !contactAgenda.isChecked


            }
        })
    }

    private fun observeAgenda() {
        addContactFromAgendaViewModel.getContactsFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { listContactAgenda ->

                listContactAgenda.add(ContactAgenda("Jaume Pescador", 342432243))
                listContactAgenda.add(ContactAgenda("Pere Cullera", 342432243))
                listContactAgenda.add(ContactAgenda("Radamel Pinetell", 342432243))
                listContactAgenda.add(ContactAgenda("Pep Gamarus", 342432243))

                listContactAgenda.forEach { contactAgenda ->
                    bizumFormViewModel.addressesRVAdapter.getListData().forEach { addressContact ->
                        if (contactAgenda.name == addressContact.name) {
                            contactAgenda.isChecked = true
                        }
                    }
                }

                addContactFromAgendaViewModel.agendaRVAdapter.setListData(listContactAgenda)
                addContactFromAgendaViewModel.agendaRVAdapter.notifyDataSetChanged()
            }
    }

    private fun goToBizumForm() {
        val bundle = bundleOf("form_type" to arguments?.getString("form_type"))
        view?.findNavController()
            ?.navigate(R.id.action_addContactFromAgendaFragment_to_bizumFormFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_add_from_agenda)
    }
}