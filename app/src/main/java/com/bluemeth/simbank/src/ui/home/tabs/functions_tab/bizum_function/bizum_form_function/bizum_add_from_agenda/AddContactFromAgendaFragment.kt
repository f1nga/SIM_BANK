package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactFromAgendaBinding
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserAddFromAgenda
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserBizum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContactFromAgendaFragment : Fragment() {

    private lateinit var binding: FragmentAddContactFromAgendaBinding
    private val addContactFromAgendaViewModel: AddContactFromAgendaViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private var contactsList: MutableList<UserBizum> = mutableListOf()

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
                for (userBizum in contactsList) {
                    bizumFormViewModel.addressesRVAdapter.setUserBizum(userBizum)
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
            override fun onItemClick(userAddFromAgenda: UserAddFromAgenda) {
                contactsList.add(
                    UserBizum(
                        name = userAddFromAgenda.name
                    )
                )
            }
        })
    }

    private fun observeAgenda() {
        addContactFromAgendaViewModel.getContactsFromDB(globalViewModel.getUserAuth().email!!)
            .observe(requireActivity()) {
                addContactFromAgendaViewModel.agendaRVAdapter.setListData(it)
                addContactFromAgendaViewModel.agendaRVAdapter.notifyDataSetChanged()
            }
    }

    private fun goToBizumForm() {
        view?.findNavController()?.navigate(R.id.action_addContactFromAgendaFragment_to_bizumFormFragment)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_add_from_agenda)
    }
}