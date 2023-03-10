package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
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
import java.util.*

@AndroidEntryPoint
class AddContactFromAgendaFragment : Fragment() {

    private lateinit var binding: FragmentAddContactFromAgendaBinding
    private val addContactFromAgendaViewModel: AddContactFromAgendaViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()

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

        binding.inputSearchText.addTextChangedListener { searchText ->
            filterRecyclerView(searchText)
        }
    }

    private fun initObservers() {
        addContactFromAgendaViewModel.navigateToBizumForm.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                addContactFromAgendaViewModel.agendaRVAdapter.listData.forEach { contactAgenda ->
                    val newContact = ContactBizum(
                        name = contactAgenda.name,
                        import = getImportArguments(),
                        phoneNumber = contactAgenda.phoneNumber
                    )

                    if (contactAgenda.isChecked) {
                        bizumFormViewModel.addressesRVAdapter.setUserBizum(newContact)
                    } else {
                        bizumFormViewModel.addressesRVAdapter.deleteUserBizum(newContact)
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
                val import = bizumFormViewModel.bizumFormModel?.import?.toDouble()

                modifyAddresseList(
                    contactAgenda.isChecked,
                    ContactBizum(
                        name = contactAgenda.name,
                        import = import ?: 0.0,
                        phoneNumber = contactAgenda.phoneNumber
                    ),
                )
            }
        })
    }

    private fun filterRecyclerView(searchText: Editable?) {
        addContactFromAgendaViewModel.getContactsFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) { listContactAgenda ->

                val listFiltered =
                    listContactAgenda.filter { contactBizum ->
                        contactBizum.name.lowercase(Locale.ROOT).contains(searchText.toString().lowercase(Locale.ROOT))
                    }
                addContactFromAgendaViewModel.agendaRVAdapter.setListData(listFiltered as MutableList<ContactAgenda>)
                addContactFromAgendaViewModel.agendaRVAdapter.notifyDataSetChanged()
            }
    }

    private fun getImportArguments(): Double {
        if (bizumFormViewModel.bizumFormArguments?.import!! != "")
            return bizumFormViewModel.bizumFormArguments?.import!!.toDouble()

        return 0.0
    }

    private fun modifyAddresseList(isChecked: Boolean, newContact: ContactBizum) {
        if (isChecked) bizumFormViewModel.addressesRVAdapter.setUserBizum(newContact)
        else bizumFormViewModel.addressesRVAdapter.deleteUserBizum(newContact)

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