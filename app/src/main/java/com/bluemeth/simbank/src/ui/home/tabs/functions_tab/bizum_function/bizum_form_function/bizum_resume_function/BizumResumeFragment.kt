package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumResumeBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BizumResumeFragment : Fragment() {

    private lateinit var binding: FragmentBizumResumeBinding
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val bizumResumeViewModel: BizumResumeViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumResumeBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setTextViews()
        setAgendaRecyclerView()
        observeAgenda()
    }

    private fun setTextViews() {
        bizumFormViewModel.bizumFormModel!!.apply {
            binding.tvTotalImport.text = this.import
            binding.tvImportEveryAddresse.text = Methods.formatMoney(this.addressesList!![0].import)
            binding.tvComision.text = "0,00€"
            if(this.subject != "") {
                binding.tvTitleSubject.isVisible = true
                binding.tvSubject.isVisible = true
                binding.tvSubject.text = this.subject
            }
        }

        globalViewModel.getBankIban().observe(requireActivity()) {
             binding.tvAccount.text = "Cuenta *${Methods.formatShortIban(it)}"
        }

    }

    private fun setAgendaRecyclerView() {
        val addressesRecycler = binding.rvAddressesResum
        addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        addressesRecycler.setHasFixedSize(true)
        addressesRecycler.adapter = bizumResumeViewModel.agendaRVAdapter

        bizumResumeViewModel.agendaRVAdapter.showCheckBox = false

        bizumResumeViewModel.agendaRVAdapter.setItemListener(object :
            AgendaRVAdapter.OnItemClickListener {
            override fun onItemClick(contactAgenda: ContactAgenda) {

            }
        })
    }

    private fun observeAgenda() {
        bizumFormViewModel.bizumFormModel!!.addressesList!!.forEach {
            bizumResumeViewModel.agendaRVAdapter.addToListData(
                ContactAgenda(
                    name = it.name,
                    phoneNumber = it.phoneNumber
                )
            )
        }
        bizumResumeViewModel.agendaRVAdapter.notifyDataSetChanged()
    }


    private fun showQuestionDialog() {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_oops),
            description = getString(R.string.dialog_error_sure),
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast("Estas apunto de cerrar tu sesion, necesitarás volver a iniciar sesión", Toast.LENGTH_LONG)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
            },
            positiveAction = QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                view?.findNavController()!!.navigate(R.id.action_bizumResumeFragment_to_bizumFragment)
            }
        ).show(dialogLauncher, requireActivity())
    }

}