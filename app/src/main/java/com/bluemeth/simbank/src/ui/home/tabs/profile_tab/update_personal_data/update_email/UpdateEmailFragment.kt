package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentUpdateEmailBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.auth.verification.VerificationActivity
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email.model.UserEmailUpdate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateEmailFragment : Fragment() {

    private lateinit var binding: FragmentUpdateEmailBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val updateEmailViewModel: UpdateEmailViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setEmail()
        initListeners()
        initObservers()
    }

    private fun initListeners() {

        binding.inputNewEmailText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewEmailText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewEmailText.onTextChanged { onFieldChanged() }

        binding.inputNewRepeatEmailText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewRepeatEmailText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewRepeatEmailText.onTextChanged { onFieldChanged() }

        binding.btnChange.setOnClickListener {
            it.dismissKeyboard()
            globalViewModel.getUserFromDB().observe(requireActivity()) { user ->
                globalViewModel.getBankIban().observe(requireActivity()) {iban ->
                    updateEmailViewModel.onChangeSelected(
                        UserEmailUpdate(
                            binding.inputNewEmailText.text.toString(),
                            binding.inputNewRepeatEmailText.text.toString(),
                        ),
                        user,
                        iban
                    )
                }
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            updateEmailViewModel.viewState.collect() { viewState ->
                updateUI(viewState)
            }
        }

        updateEmailViewModel.showDialog.observe(requireActivity()) { showError ->
            if (showError) showSuccesDialog()
        }
    }

    private fun showSuccesDialog() {
        SuccessDialog.create(
            getString(R.string.email_changed),
            getString(R.string.go_to_verify_account),
            SuccessDialog.Action(getString(R.string.dialog_verified_positive)) {
                it.dismiss()
                goToVerifyEmail()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun updateUI(viewState: UpdateEmailViewState) {
        binding.inputNewEmail.error =
            if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
        binding.inputNewRepeatEmail.error =
            if (viewState.isValidEmailConfirm) null else getString(R.string.signin_error_password)

    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            updateEmailViewModel.onFieldsChanged(
                UserEmailUpdate(
                    binding.inputNewEmailText.text.toString(),
                    binding.inputNewRepeatEmailText.text.toString()
                )
            )
        }
    }

    private fun setEmail() {
        binding.tvActualEmail.text = globalViewModel.getUserAuth().email!!
    }

    private fun goToVerifyEmail() {
        val intent = Intent(requireContext(), VerificationActivity::class.java)
        intent.putExtra("destination", "home")
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_update_email)
    }

}