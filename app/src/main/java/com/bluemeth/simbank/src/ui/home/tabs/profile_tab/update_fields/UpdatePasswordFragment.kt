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
import com.bluemeth.simbank.databinding.FragmentUpdatePasswordBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states.UpdatePasswordViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private lateinit var binding: FragmentUpdatePasswordBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val updatePasswordViewModel: UpdatePasswordViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {

        binding.inputNewPasswordText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewPasswordText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewPasswordText.onTextChanged { onFieldChanged() }

        binding.inputRepeatNewPasswordText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputRepeatNewPasswordText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputRepeatNewPasswordText.onTextChanged { onFieldChanged() }

        with(binding) {
            btnNext.setOnClickListener {
                it.dismissKeyboard()
                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                    updatePasswordViewModel.onNextSelected(
                        user.password,
                        binding.inputActualPasswordText.text.toString()
                    )
                }
            }
        }

        with(binding) {
            btnChange.setOnClickListener {
                it.dismissKeyboard()
                globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                    updatePasswordViewModel.onChangeSelected(
                        binding.inputNewPasswordText.text.toString(),
                        binding.inputRepeatNewPasswordText.text.toString(),
                        user.password
                    )
                }

            }
        }
    }

    private fun initObservers() {
        updatePasswordViewModel.navigateToProfile.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let {
                goToProfile()
            }
        }

        updatePasswordViewModel.showPasswordsNotMatchDialog.observe(requireActivity()) { showError ->
            if (showError) showErrorDialog(getString(R.string.password_actual))
        }

        updatePasswordViewModel.showCoincideOldPasswordDialog.observe(requireActivity()) { showError ->
            if (showError) showErrorDialog(getString(R.string.password_not_match))
        }

        updatePasswordViewModel.goToNextForm.observe(requireActivity()) {
            changeForm()
        }

        lifecycleScope.launchWhenStarted {
            updatePasswordViewModel.viewPasswordState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun showErrorDialog(body: String) {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = body,
            positiveAction = ErrorDialog.Action(getString(R.string.try_again)) {
                it.dismiss()
            },
            negativeAction = ErrorDialog.Action(getString(R.string.back)) {
                it.dismiss()
                view?.findNavController()
                    ?.navigate(R.id.action_updatePasswordFragment_to_profileFragment)
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun changeForm() {
        binding.inputActualPassword.visibility = View.GONE
        binding.ivInfo.visibility = View.GONE
        binding.tvInfo.visibility = View.GONE
        binding.btnNext.visibility = View.GONE

        binding.tvNewPasswordTitle.text = "NUEVA CONTRASEÑA"

        binding.inputNewPassword.visibility = View.VISIBLE
        binding.inputRepeatNewPassword.visibility = View.VISIBLE
        binding.btnChange.visibility = View.VISIBLE

    }

    private fun updateUI(viewState: UpdatePasswordViewState) {
        val textError =
            if (!viewState.isValidPasswordLength) getString(R.string.signin_error_nickname)
            else if (!viewState.isValidPasswordConfirmation) getString(R.string.signin_error_password)
            else null
        binding.inputNewPassword.error = textError

        binding.inputRepeatNewPassword.error =
            if (viewState.isValidPasswordConfirmation) null else getString(R.string.signin_error_password)
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            globalViewModel.getUserFromDB().observe(requireActivity()) {
                updatePasswordViewModel.onFieldsChanged(
                    binding.inputNewPasswordText.text.toString(),
                    binding.inputRepeatNewPasswordText.text.toString(),
                    it.password
                )
            }

        }
    }

    private fun goToProfile() {
        view?.findNavController()?.navigate(R.id.action_updatePhoneFragment_to_profileFragment)
    }
}