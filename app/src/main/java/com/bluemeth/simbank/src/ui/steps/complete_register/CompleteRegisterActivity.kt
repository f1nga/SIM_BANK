package com.bluemeth.simbank.src.ui.steps.complete_register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityCompleteRegisterBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.steps.StepsActivity
import com.bluemeth.simbank.src.ui.steps.complete_register.model.UserCompleteRegister
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompleteRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteRegisterBinding
    private val completeRegisterViewModel: CompleteRegisterViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    companion object {
        fun create(context: Context): Intent =
            Intent(context, CompleteRegisterActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {

        binding.inputNewNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewNameText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNewNameText.onTextChanged { onFieldChanged() }

        binding.inputNewLastNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewLastNameText.setOnFocusChangeListener { _, hasFocus ->
            onFieldChanged(
                hasFocus
            )
        }
        binding.inputNewLastNameText.onTextChanged { onFieldChanged() }

        binding.inputNewSecondNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNewSecondNameText.setOnFocusChangeListener { _, hasFocus ->
            onFieldChanged(
                hasFocus
            )
        }
        binding.inputNewSecondNameText.onTextChanged { onFieldChanged() }

        binding.inputPhoneText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputPhoneText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputPhoneText.onTextChanged { onFieldChanged() }

        binding.btnComplete.setOnClickListener {
            completeRegisterViewModel.onCompleteSelected(
                UserCompleteRegister(
                    name = binding.inputNewNameText.text.toString(),
                    lastName = binding.inputNewLastNameText.text.toString(),
                    secondName = binding.inputNewSecondNameText.text.toString(),
                    phoneNumber = binding.inputPhoneText.text.toString(),
                )
            )
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            completeRegisterViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        completeRegisterViewModel.navigateToSteps.observe(this) {
            it.getContentIfNotHandled()?.let {
                showSuccesDialog()
            }
        }

        completeRegisterViewModel.showErrorDialog.observe(this) {
            if (it) showErrorDialog()
        }
    }

    private fun updateUI(viewState: UserCompleteRegisterViewState) {
        binding.inputNewName.error =
            if (viewState.isValidName) null else getString(R.string.minim_three_characters)
        binding.inputNewLastName.error =
            if (viewState.isValidLastName) null else getString(R.string.minim_three_characters)
        binding.inputNewSecondName.error =
            if (viewState.isValidSecondName) null else getString(R.string.minim_three_characters)
        binding.inputPhone.error =
            if (viewState.isValidPhoneNumber) null else getString(R.string.phone_no_valid)

    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            completeRegisterViewModel.onNameFieldsChanged(
                UserCompleteRegister(
                    name = binding.inputNewNameText.text.toString(),
                    lastName = binding.inputNewLastNameText.text.toString(),
                    secondName = binding.inputNewSecondNameText.text.toString(),
                    phoneNumber = binding.inputPhoneText.text.toString()
                )
            )
        }
    }
    private fun showSuccesDialog() {
        SuccessDialog.create(
            getString(R.string.dialog_verified_title),
            getString(R.string.dialog_complete_register_body),
            SuccessDialog.Action(getString(R.string.dialog_verified_positive)) { goToSteps() }
        ).show(dialogLauncher, this)
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.complete_register_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToSteps() {
        startActivity(StepsActivity.create(this))
    }
}