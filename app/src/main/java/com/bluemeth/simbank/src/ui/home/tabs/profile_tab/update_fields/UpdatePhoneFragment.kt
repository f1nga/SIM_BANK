package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentUpdatePhoneBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.ProfileViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdatePhoneViewState
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePhoneFragment : Fragment() {

    private lateinit var binding: FragmentUpdatePhoneBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePhoneBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setNumberPhone()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.inputNewPhoneText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewPhoneText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewPhoneText.onTextChanged { onFieldChanged() }

        with(binding) {
            btnChange.setOnClickListener {
                it.dismissKeyboard()
                profileViewModel.onChangeSelected(
                    binding.inputNewPhoneText.text.toString()
                )
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        profileViewModel.navigateToProfile.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToProfile()
            }
        }
    }

    private fun updateUI(viewState: UpdatePhoneViewState) {
        with(binding) {
            binding.inputNewPhone.error =
                if (viewState.isValidPhoneNumber) null else "El teléfono no es válido"
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            profileViewModel.onFieldsChanged(
                binding.inputNewPhoneText.text.toString()
            )
        }
    }

    private fun setNumberPhone() {
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            binding.tvActualPhone.text = Methods.formatPhoneNumber(it.phone)
        }
    }

    private fun goToProfile() {
        view?.findNavController()?.navigate(R.id.action_updatePhoneFragment_to_profileFragment)
    }
}