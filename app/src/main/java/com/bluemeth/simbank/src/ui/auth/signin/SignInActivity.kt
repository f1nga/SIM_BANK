package com.bluemeth.simbank.src.ui.auth.signin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityRegisterBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.bluemeth.simbank.src.ui.auth.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }

    private lateinit var binding: ActivityRegisterBinding
    private val signInViewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {

        binding.inputEmailRegistreText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputEmailRegistreText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputEmailRegistreText.onTextChanged { onFieldChanged() }

        binding.inputNicknameRegistreText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputNicknameRegistreText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputNicknameRegistreText.onTextChanged { onFieldChanged() }

        binding.inputPasswordRegistreText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputPasswordRegistreText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputPasswordRegistreText.onTextChanged { onFieldChanged() }

        binding.inputRepeatPasswordRegistreText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.inputRepeatPasswordRegistreText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputRepeatPasswordRegistreText.onTextChanged { onFieldChanged() }

        binding.inputTelefonRegistreText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputTelefonRegistreText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputTelefonRegistreText.onTextChanged { onFieldChanged() }

        binding.txtFinal.setOnClickListener{ signInViewModel.onLoginSelected() }

        with(binding) {
            btnRegister.setOnClickListener {
                it.dismissKeyboard()
                signInViewModel.onSignInSelected(
                    UserSignIn(
                        nickName = binding.inputNicknameRegistreText.text.toString(),
                        email = binding.inputEmailRegistreText.text.toString(),
                        password = binding.inputPasswordRegistreText.text.toString(),
                        passwordConfirmation = binding.inputRepeatPasswordRegistreText.text.toString(),
                        phoneNumber = binding.inputTelefonRegistreText.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {
       signInViewModel.navigateToVerifyEmail.observe(this) {
           it.getContentIfNotHandled()?.let {
               goToVerifyEmail()
           }
        }

        signInViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        lifecycleScope.launchWhenStarted {
            signInViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signInViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.signin_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            binding.inputEmailRegistre.error =
                if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
            binding.inputNicknameRegistre.error =
                if (viewState.isValidNickName) null else getString(R.string.signin_error_nickname)
            binding.inputPasswordRegistre.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            binding.inputRepeatPasswordRegistre.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            binding.inputTelefonRegistre.error =
                if (viewState.isValidPhoneNumber) null else "El teléfono no es válido"
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            signInViewModel.onFieldsChanged(
                UserSignIn(
                    nickName = binding.inputNicknameRegistreText.text.toString(),
                    email = binding.inputEmailRegistreText.text.toString(),
                    password = binding.inputPasswordRegistreText.text.toString(),
                    passwordConfirmation = binding.inputRepeatPasswordRegistreText.text.toString(),
                    phoneNumber = binding.inputTelefonRegistreText.text.toString()
                )
            )
        }
    }

   private fun goToVerifyEmail() {
       startActivity(VerificationActivity.create(this))
   }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}