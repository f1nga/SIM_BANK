package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumFormBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BizumFormFragment : Fragment() {

    private lateinit var binding: FragmentBizumFormBinding
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()

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
            inputImportText.setOnFocusChangeListener { _, hasFocus -> formatInput(hasFocus) }
            inputImportText.onTextChanged { onFieldChanged() }

            inputSubjectText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputSubjectText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputSubjectText.onTextChanged { onFieldChanged() }

            llAddFromAgenda.setOnClickListener {
                saveFormArguments()
                setImportsToAddressList()
                goToAddContactFromAgenda()
            }

            llAddManually.setOnClickListener {
                saveFormArguments()
                setImportsToAddressList()
                goToAddContactManually()
            }

            btnContinue.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    bizumFormViewModel.viewState.collect { viewState ->
                        log("hool", "hooool")
                        if (!viewState.isValidAddressesList) tvErrorAddresses.isVisible = true
                    }
                }

                val newImport = if (inputImportText.text.toString().contains("€")) {
                    Methods.splitEuro(inputImportText.text.toString())
                } else inputImportText.text.toString()

                bizumFormViewModel.onContinueSelected(
                    BizumFormModel(
                        import = newImport,
                        subject = inputSubjectText.text.toString(),
                        addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
                    )
                )
            }
        }
    }

    private fun formatInput(hasFocus: Boolean) {
        val inputText = Editable.Factory.getInstance()

        binding.inputImportText.apply {
            if (this.toString().isNotEmpty()) {
                if (hasFocus) {
//                if (/*&& importText.contains("€")*/) {
                    text = inputText.newEditable(Methods.splitEuro(text.toString()))
//                }
                } else {
                    text =
                        inputText.newEditable(Methods.formatMoney(Methods.roundOffDecimal(text.toString().toDouble())))
                    setImportsToAddressList()
                }
            }
        }
    }

    private fun initObservers() {
        bizumFormViewModel.navigateToBizumResum.observe(requireActivity()) {
            setImportsToAddressList()

            bizumFormViewModel.setBizumFormModel(
                BizumFormModel(
                    import = binding.tvTotalEnvio.text.toString(),
                    subject = binding.inputSubjectText.text.toString(),
                    addressesList = bizumFormViewModel.addressesRVAdapter.getListData()              )
            )

            goToBizumResume()
        }

        lifecycleScope.launchWhenStarted {
            bizumFormViewModel.viewState.collect { viewState ->
                updateUI(viewState)
                setImportsToAddressList()
            }
        }

    }

    private fun updateUI(viewState: BizumFormViewState) {
        with(binding) {
            inputSubject.error =
                if (viewState.isValidSubject) null else "El asunto es demasiado largo"
            if (viewState.isValidAddressesList) tvErrorAddresses.isVisible = false
            if (viewState.isValidImport) inputImport.error =
                null else inputImport.error = "Error"
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        with(binding) {
            if (!hasFocus) {
                val characters = 35 - inputSubjectText.text.toString().length

                if (characters >= 0) {
                    tvCharacters.setTextColor(Color.parseColor("#FFFFFF"))
                    tvCharacters.text = "$characters caracteres"
                } else {
                    tvCharacters.setTextColor(Color.parseColor("#e84545"))
                    tvCharacters.text = "Has sobrepasado el límite"
                }

                val newImport = if (inputImportText.text.toString().contains("€")) {
                    Methods.splitEuro(inputImportText.text.toString())
                } else {
                    inputImportText.text.toString()
                }

                bizumFormViewModel.onNameFieldsChanged(

                    BizumFormModel(
                        import = newImport,
                        subject = inputSubjectText.text.toString(),
                        addressesList = bizumFormViewModel.addressesRVAdapter.getListData()
                    )
                )
            }
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

    private fun setImportsToAddressList() {
        if (binding.inputImportText.text.toString().isNotEmpty()) {
            bizumFormViewModel.addressesRVAdapter
                .setImportsToContacts(Methods.splitEuroDouble(binding.inputImportText.text.toString()))

            observeAddresses()
        }
    }

    private fun goToAddContactManually() {
        val bundle = bundleOf("form_type" to arguments?.getString("form_type"))
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactManuallyFragment, bundle)
    }

    private fun goToAddContactFromAgenda() {
        val bundle = bundleOf("form_type" to arguments?.getString("form_type"))
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactFromAgendaFragment, bundle)
    }

    private fun saveFormArguments() {
        bizumFormViewModel.setBizumFormArguments(
            BizumFormModel(
                import = Methods.splitEuro(binding.inputImportText.text.toString()).ifEmpty { "" },
                subject = binding.inputSubjectText.text.toString().ifEmpty { "" },
                addressesList = bizumFormViewModel.addressesRVAdapter.getListData().ifEmpty { null }
            )
        )
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