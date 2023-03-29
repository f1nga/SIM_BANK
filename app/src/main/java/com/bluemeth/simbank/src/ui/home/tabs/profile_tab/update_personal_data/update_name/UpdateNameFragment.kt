package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentUpdateNameBinding
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_name.model.UserNameUpdate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateNameFragment : Fragment() {

    private lateinit var binding: FragmentUpdateNameBinding
    private val updateNameViewModel: UpdateNameViewModel by viewModels()
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

        binding.btnChange.setOnClickListener {
            it.dismissKeyboard()
            globalViewModel.getUserName().observe(requireActivity()) {
                updateNameViewModel.onChangeNameSelected(
                    UserNameUpdate(
                        name = binding.inputNewNameText.text.toString(),
                        lastName = binding.inputNewLastNameText.text.toString(),
                        secondName = binding.inputNewSecondNameText.text.toString()
                    ),
                    it
                )
            }

        }

    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            updateNameViewModel.viewNameState.collect { viewState ->
                updateUI(viewState)
            }
        }

        updateNameViewModel.navigateToProfile.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                toast("El nombre de usuario ha sido actualizado")
                goToProfile()
            }
        }
    }

    private fun updateUI(viewState: UpdateNameViewState) {
        binding.inputNewName.error =
            if (viewState.isValidName) null else getString(R.string.minim_three_characters)
        binding.inputNewLastName.error =
            if (viewState.isValidLastName) null else getString(R.string.minim_three_characters)
        binding.inputNewSecondName.error =
            if (viewState.isValidSecondName) null else getString(R.string.minim_three_characters)

    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            updateNameViewModel.onNameFieldsChanged(
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

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_update_name)
    }
}