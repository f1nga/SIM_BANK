package com.bluemeth.simbank.src.ui.auth.verification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.ActivityVerificationBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.SuccessDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, VerificationActivity::class.java)
    }

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    private lateinit var binding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnGoToDetail.setOnClickListener { verificationViewModel.onGoToDetailSelected() }
    }

    private fun initObservers() {
        verificationViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                SuccessDialog.create(
                    getString(R.string.dialog_verified_title),
                    getString(R.string.dialog_verified_body),
                    SuccessDialog.Action(getString(R.string.dialog_verified_positive)) { goToLogin() }
                ).show(dialogLauncher, this)
            }
        }

        verificationViewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.btnGoToDetail.isVisible = true
            }
        }

        verificationViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

}