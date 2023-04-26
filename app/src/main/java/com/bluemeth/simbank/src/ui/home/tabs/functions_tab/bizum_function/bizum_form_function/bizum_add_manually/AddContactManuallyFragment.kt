package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddContactManuallyBinding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.BizumFormViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually.model.ContactManually
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContactManuallyFragment : Fragment() {

    private lateinit var binding: FragmentAddContactManuallyBinding
    private val addContactManuallyViewModel: AddContactManuallyViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()
    private val bizumFormViewModel: BizumFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactManuallyBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            inputPhoneText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputPhoneText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputPhoneText.onTextChanged { onFieldChanged() }

            inputConfirmPhoneText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            inputConfirmPhoneText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            inputConfirmPhoneText.onTextChanged { onFieldChanged() }

            btnAddAddresse.setOnClickListener {
                addContactManuallyViewModel.onAddContactSelected(
                    ContactManually(
                        phoneNumber = inputPhoneText.text.toString(),
                        phoneNumberConfirm = inputConfirmPhoneText.text.toString()
                    )
                )
            }
        }

    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            addContactManuallyViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        addContactManuallyViewModel.navigateToBizumForm.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {

                addToAddressesList(
                    ContactBizum(
                        phoneNumber = binding.inputPhoneText.text.toString().toInt(),
                        import = bizumFormViewModel.bizumFormArgument!!.let {
                            if (it.import.isEmpty()) 0.0 else it.import.toDouble()
                        }
                    )
                )
                goToBizumForm()
            }
        }
    }

    private fun updateUI(viewState: AddContactManuallyViewState) {
        binding.inputPhone.error =
            if (viewState.isValidPhone) null else "El formato no es válido"
        binding.inputConfirmPhone.error =
            if (viewState.isValidPhoneConfirm) null else "Los campos deben coincidir"
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            addContactManuallyViewModel.onNameFieldsChanged(
                ContactManually(
                    phoneNumber = binding.inputPhoneText.text.toString(),
                    phoneNumberConfirm = binding.inputConfirmPhoneText.text.toString(),
                )
            )
        }
    }

    private fun addToAddressesList(contactBizum: ContactBizum) {
        bizumFormViewModel.setContactBizum(contactBizum)
    }

    private fun goToBizumForm() {
        val bundle = bundleOf(Constants.FORM_TYPE to arguments?.getString(Constants.FORM_TYPE))
        view?.findNavController()
            ?.navigate(R.id.action_addContactManuallyFragment_to_bizumFormFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_add_manually)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_addContactManuallyFragment_to_bizumFormFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}