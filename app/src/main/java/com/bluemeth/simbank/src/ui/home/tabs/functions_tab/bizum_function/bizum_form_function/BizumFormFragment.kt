package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumFormBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function.BizumResumeViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BizumFormFragment : Fragment() {

    private lateinit var binding: FragmentBizumFormBinding
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val bizumResumeViewModel: BizumResumeViewModel by activityViewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBizumFormBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setUserBizumRecyclerView()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            inputImportText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputImportText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputImportText.onTextChanged { onFieldChanged() }

            inputSubjectText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputSubjectText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputSubjectText.onTextChanged { onFieldChanged() }

            llAddFromAgenda.setOnClickListener {
                saveFormArguments()
                goToAddContactFromAgenda()
            }

            llAddManually.setOnClickListener {
                saveFormArguments()
                goToAddContactManually()
            }

            btnContinue.setOnClickListener {
                bizumFormViewModel.onContinueSelected(
                    BizumFormModel(
//                        import = Methods.splitEuro(binding.inputImportText.text.toString()) ,
                        import = binding.inputImportText.text.toString(),
                        subject = inputSubjectText.text.toString(),
                        addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
                    )
                )
            }
        }
    }

    private fun initObservers() {
        bizumFormViewModel.navigateToBizumResum.observe(requireActivity()) {

//                bizumResumeViewModel.setBizumFormModel(
//                    BizumFormModel(
//                        import = binding.tvTotalEnvio.text.toString(),
//                        subject = binding.inputSubjectText.text.toString(),
//                        addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
//                    )
//                )
            setImportsToRecyclerView()

            bizumFormViewModel.setBizumFormModel(
                BizumFormModel(
                    import = binding.tvTotalEnvio.text.toString(),
                    subject = binding.inputSubjectText.text.toString(),
                    addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
                )
            )

            goToBizumResume()

        }

//        bizumFormViewModel.showAddresseErrorDialog.observe(requireActivity()) {
//            if (it) showErrorDialog("Debes añadir mínimo un destinatario")
//        }
//
//        bizumFormViewModel.showImportErrorDialog.observe(requireActivity()) {
//            if (it) showErrorDialog("Debes especificar el importe")
//        }

    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        var importText = binding.inputImportText.text!!
        if (!hasFocus) {
            bizumFormViewModel.onNameFieldsChanged(
                BizumFormModel(
//                    import = Methods.splitEuro(binding.inputImportText.text.toString()) ,
                    import = binding.inputImportText.text.toString(),
                    subject = binding.inputSubjectText.text.toString(),
                    addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
                )
            )
        } else {
//            if (importText.isNotEmpty()) {
//                val inputText = Editable.Factory.getInstance()
//                binding.inputImportText.text =
//                    inputText.newEditable(Methods.formatMoney(importText.toString().toDouble()))
            setImportsToRecyclerView()
//            }
        }
    }

    private fun setUserBizumRecyclerView() {

        val addressesRecycler = binding.rvAddresses
        addressesRecycler.layoutManager = LinearLayoutManager(requireContext())
        addressesRecycler.setHasFixedSize(true)
        addressesRecycler.adapter = bizumFormViewModel.addressesRVAdapter

        bizumFormViewModel.addressesRVAdapter.setItemListener(object :
            AddressesRVAdapter.OnItemClickListener {
            override fun onItemClick(contactBizum: ContactBizum) {
                bizumFormViewModel.addressesRVAdapter.deleteUserBizum(contactBizum)
                observeAddresses()
            }
        })
        observeAddresses()
    }

    private fun observeAddresses() {
        binding.tvTotalEnvio.text =
            Methods.formatMoney(bizumFormViewModel.addressesRVAdapter.getTotalImport())
        bizumFormViewModel.addressesRVAdapter.notifyDataSetChanged()
    }

    private fun setImportsToRecyclerView() {
        if (binding.inputImportText.text.toString().isNotEmpty()) {
            bizumFormViewModel.addressesRVAdapter
                .setImportsToContacts(binding.inputImportText.text.toString().toDouble())
//            bizumFormViewModel.addressesRVAdapter
//                .setImportsToContacts(Methods.splitEuroDouble(binding.inputImportText.text.toString()))
            observeAddresses()
        }
    }

    private fun goToAddContactManually() {
        setImportsToRecyclerView()
        val bundle = bundleOf("form_type" to arguments?.getString("form_type"))
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactManuallyFragment, bundle)
    }

    private fun goToAddContactFromAgenda() {
        setImportsToRecyclerView()
        val bundle = bundleOf("form_type" to arguments?.getString("form_type"))
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactFromAgendaFragment, bundle)
    }

    private fun saveFormArguments() {
        bizumFormViewModel.setBizumFormArguments(
            BizumFormModel(
                import = binding.inputImportText.text.toString().ifEmpty { "" },
                subject = binding.inputSubjectText.text.toString().ifEmpty { "" },
                addressesList = bizumFormViewModel.addressesRVAdapter.getListData().ifEmpty { null }
            )
        )
    }

    private fun showErrorDialog(description: String) {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = description,
            positiveAction = ErrorDialog.Action(getString(R.string.try_again)) {
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun goToBizumResume() {
        view?.findNavController()?.navigate(R.id.action_bizumFormFragment_to_bizumResumeFragment)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView
        tvTitle.text = "Bizum"

        binding.tvTitleForm.text = arguments?.getString("form_type")

        val inputText = Editable.Factory.getInstance()

        bizumFormViewModel.bizumFormArguments?.let {
            binding.inputImportText.text = inputText.newEditable(it.import)
            binding.inputSubjectText.text = inputText.newEditable(it.subject)
        }
    }

}