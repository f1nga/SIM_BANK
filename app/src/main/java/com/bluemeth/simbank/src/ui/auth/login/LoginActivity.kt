package com.bluemeth.simbank.src.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityLoginBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ErrorDialog
import com.bluemeth.simbank.src.core.ex.dismissKeyboard
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.auth.forgot_password.ForgotPasswordActivity
import com.bluemeth.simbank.src.ui.auth.login.model.UserLogin
import com.bluemeth.simbank.src.ui.auth.signin.SignInActivity
import com.bluemeth.simbank.src.ui.auth.verification.VerificationActivity
import com.bluemeth.simbank.src.ui.home.HomeActivity
import com.bluemeth.simbank.src.ui.steps.StepsActivity
import com.bluemeth.simbank.src.ui.steps.complete_register.CompleteRegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val GOOGLE_SIGN_IN = 100

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // prefs.saveSteps()
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
        binding.btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        }
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

        loginViewModel.navigateToCompleteRegister.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToCompleteRegister()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            try {
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    loginViewModel.onGoogleLoginSelected(credential)
                }
            } catch (e: ApiException) {
                Timber.tag("hol").i(":(")
            }
        }

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

    private fun goToSteps() {
        startActivity(StepsActivity.create(this))
    }

    private fun goToCompleteRegister() {
        startActivity(CompleteRegisterActivity.create(this))
    }
}