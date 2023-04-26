package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import kotlin.collections.emptyList
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentBizumFormBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BizumFormFragment : Fragment() {

    private lateinit var binding: FragmentBizumFormBinding
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

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
        getSavedArguments()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            inputImportText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputImportText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputImportText.setOnFocusChangeListener { _, hasFocus -> formatInputImport(hasFocus) }
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
                        if (!viewState.isValidAddresse) tvErrorAddresses.isVisible = true
                    }
                }

                val newImport = if (inputImportText.text.toString().contains("€")) {
                    Methods.splitEuro(inputImportText.text.toString())
                } else inputImportText.text.toString()

                bizumFormViewModel.onContinueSelected(
                    BizumFormModel(
                        import = newImport,
                        subject = inputSubjectText.text.toString(),
                        addresse = if (bizumFormViewModel.addressesRVAdapter.getListData()
                                .isNotEmpty()
                        ) bizumFormViewModel.addressesRVAdapter.getListData()[0] else null
                    )
                )
            }
        }
    }

    private fun formatInputImport(hasFocus: Boolean) {
        val inputText = Editable.Factory.getInstance()

        binding.inputImportText.apply {
            if (text.toString().isNotEmpty()) {
                if (hasFocus) {
                    text = inputText.newEditable(Methods.splitEuro(text.toString()))
                } else {
                    text =
                        inputText.newEditable(
                            Methods.formatMoney(
                                Methods.roundOffDecimal(
                                    text.toString().toDouble()
                                )
                            )
                        )
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
                    addresse = if (bizumFormViewModel.addressesRVAdapter.getListData()
                            .isNotEmpty()
                    ) bizumFormViewModel.addressesRVAdapter.getListData()[0] else null
                )
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
            if (viewState.isValidAddresse) tvErrorAddresses.isVisible = false
            if (viewState.isValidImport) inputImport.error =
                null else inputImport.error = "Error"
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        with(binding) {
            if (!hasFocus) {
                val characters = 35 - inputSubjectText.text.toString().length

                tvCharacters.apply {
                    text = if (characters >= 0) {
                        setTextColor(Color.parseColor("#FFFFFF"))
                        "$characters caracteres"
                    } else {
                        setTextColor(Color.parseColor("#e84545"))
                        "Has sobrepasado el límite"
                    }
                }

                bizumFormViewModel.onFieldsChanged(
                    BizumFormModel(
                        import = if (inputImportText.text.toString().contains("€")) {
                            Methods.splitEuro(inputImportText.text.toString())
                        } else {
                            inputImportText.text.toString()
                        },
                        subject = inputSubjectText.text.toString(),
                        addresse = if (bizumFormViewModel.addressesRVAdapter.getListData()
                                .isNotEmpty()
                        ) bizumFormViewModel.addressesRVAdapter.getListData()[0] else null
                    )
                )
            }
        }
    }

    private fun getSavedArguments() {
        val inputText = Editable.Factory.getInstance()

        bizumFormViewModel.reUseBizumArgument?.let {

            binding.inputSubjectText.text = inputText.newEditable(it.subject)
            bizumFormViewModel.addressesRVAdapter.setListData(mutableListOf(it.addresse!!))


            if(it.import.isNotEmpty()) {
                binding.inputImportText.text = inputText.newEditable(
                    Methods.formatMoney(
                        Methods.roundOffDecimal(
                            it.import.toDouble()
                        )
                    )
                )
            }

        }

        bizumFormViewModel.bizumFormArgument?.let {
            binding.inputImportText.text = inputText.newEditable(it.import)
            binding.inputSubjectText.text = inputText.newEditable(it.subject)
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

    @SuppressLint("NotifyDataSetChanged")
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
        val bundle = bundleOf(Constants.FORM_TYPE to arguments?.getString(Constants.FORM_TYPE))
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactManuallyFragment, bundle)
    }

    private fun goToAddContactFromAgenda() {
        val bundle = bundleOf(
            Constants.FORM_TYPE to arguments?.getString(Constants.FORM_TYPE),
            "coming_from" to "bizum_form"
        )
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_addContactFromAgendaFragment, bundle)
    }

    private fun saveFormArguments() {
        bizumFormViewModel.setBizumFormArguments(
            BizumFormModel(
                import = Methods.splitEuro(binding.inputImportText.text.toString()).ifEmpty { "" },
                subject = binding.inputSubjectText.text.toString().ifEmpty { "" },
                addresse = if (bizumFormViewModel.addressesRVAdapter.getListData()
                        .isNotEmpty()
                ) bizumFormViewModel.addressesRVAdapter.getListData()[0] else null
            )
        )
    }

    private fun goToBizumResume() {
        val bundle = bundleOf(
            Constants.FORM_TYPE to arguments?.getString(Constants.FORM_TYPE),
            Constants.REUSE to arguments?.getBoolean(Constants.REUSE)
        )
        view?.findNavController()
            ?.navigate(R.id.action_bizumFormFragment_to_bizumResumeFragment, bundle)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView
        tvTitle.text = getString(R.string.toolbar_bizum_form)

        binding.tvTitleForm.text = arguments?.getString(Constants.FORM_TYPE)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_bizumFormFragment_to_notificationsFragment)
            }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!)
                .observe(requireActivity()) { isReaded ->
                    it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
                }
        }
    }
}