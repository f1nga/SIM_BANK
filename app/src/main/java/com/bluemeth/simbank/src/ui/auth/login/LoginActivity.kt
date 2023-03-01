package com.bluemeth.simbank.src.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.src.ui.auth.login.model.UserLogin
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityLoginBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.ex.*
import com.bluemeth.simbank.src.ui.auth.forgot_password.ForgotPasswordActivity
import com.bluemeth.simbank.src.ui.auth.signin.SignInActivity
import com.bluemeth.simbank.src.ui.auth.verification.VerificationActivity
import com.bluemeth.simbank.src.ui.home.HomeActivity
import com.bluemeth.simbank.src.ui.steps.StepsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.inputEmailText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputEmailText.onTextChanged { onFieldChanged() }

        binding.inputPasswordText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.inputPasswordText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputPasswordText.onTextChanged { onFieldChanged() }

        binding.textViewForgot.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

        binding.txtFinal.setOnClickListener { loginViewModel.onSignInSelected() }

        binding.btnLogin.setOnClickListener {
            it.dismissKeyboard()
            loginViewModel.onLoginSelected(
                binding.inputEmailText.text.toString(),
                binding.inputPasswordText.text.toString(),
                binding.cbRemember.isChecked
            )
            loginViewModel.navigateToSteps
        }
    }

    private fun initObservers() {
        loginViewModel.navigateToHome.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }

        loginViewModel.navigateToForgotPassword.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToForgotPassword()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerify()
            }
        }

        loginViewModel.navigateToSteps.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSteps()
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }


        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            inputEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            inputPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            loginViewModel.onFieldsChanged(
                email = binding.inputEmailText.text.toString(),
                password = binding.inputPasswordText.text.toString()
            )
        }
    }

    private fun showErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.login_error_dialog_title),
            description = getString(R.string.login_error_dialog_body),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onLoginSelected(
                    userLogin.email,
                    userLogin.password,
                    binding.cbRemember.isChecked
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToForgotPassword() {
        startActivity(ForgotPasswordActivity.create(this))
    }

    private fun goToSignIn() {
        startActivity(SignInActivity.create(this))
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(this))
    }

    private fun goToVerify() {
        startActivity(VerificationActivity.create(this))
    }

    private fun goToSteps(){
        startActivity(StepsActivity.create(this))
    }
}