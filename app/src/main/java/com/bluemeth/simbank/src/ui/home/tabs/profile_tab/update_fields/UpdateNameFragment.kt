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
import com.bluemeth.simbank.databinding.FragmentUpdateNameBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.ProfileViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.models.UserNameUpdate
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdateNameViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateNameFragment : Fragment() {

    private lateinit var binding: FragmentUpdateNameBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateNameBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setUserName()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.inputNewNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewNameText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewNameText.onTextChanged { onFieldChanged() }

        binding.inputNewLastNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewLastNameText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewLastNameText.onTextChanged { onFieldChanged() }

        binding.inputNewSecondNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewSecondNameText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewSecondNameText.onTextChanged { onFieldChanged() }

        with(binding) {
            btnChange.setOnClickListener {
                it.dismissKeyboard()
                profileViewModel.onChangeNameSelected(
                    UserNameUpdate(
                        name = binding.inputNewNameText.text.toString(),
                        lastName = binding.inputNewLastNameText.text.toString(),
                        secondName = binding.inputNewSecondNameText.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.viewNameState.collect { viewState ->
                updateUI(viewState)
            }
        }

        profileViewModel.navigateToProfile.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToProfile()
            }
        }
    }

    private fun updateUI(viewState: UpdateNameViewState) {
        with(binding) {
            binding.inputNewName.error =
                if (viewState.isValidName) null else "Debe contener al menos 3 carácteres"
            binding.inputNewLastName.error =
                if (viewState.isValidLastName) null else "Debe contener al menos 3 carácteres"
            binding.inputNewSecondName.error =
                if (viewState.isValidSecondName) null else "Debe contener al menos 3 carácteres"
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            profileViewModel.onNameFieldsChanged(
                UserNameUpdate(
                    name = binding.inputNewNameText.text.toString(),
                    lastName = binding.inputNewLastNameText.text.toString(),
                    secondName = binding.inputNewSecondNameText.text.toString(),
                )
            )
        }
    }

    private fun setUserName() {
        globalViewModel.getUserName().observe(requireActivity()) {
            binding.tvActualName.text = it
        }
    }

    private fun goToProfile() {
        view?.findNavController()?.navigate(R.id.action_updatePhoneFragment_to_profileFragment)
    }
}