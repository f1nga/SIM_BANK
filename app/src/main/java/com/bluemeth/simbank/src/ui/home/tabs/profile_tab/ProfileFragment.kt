package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentProfileBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.QuestionDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.auth.login.LoginActivity
import com.bluemeth.simbank.src.ui.welcome.WelcomeActivity
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var imageReference  = Firebase.storage.reference
    private var currentFile: Uri? = null;
    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        setPersonalData()
        changeImage()
        initListeners()
        initObservers()
    }

    private fun setPersonalData() {
        with(Editable.Factory.getInstance()) {
            globalViewModel.getUserFromDB().observe(requireActivity()) {
                binding.inputProfileEmailText.text = newEditable(it.email)
                binding.inputProfilePhoneText.text =
                    newEditable(Methods.formatPhoneNumber(it.phone))
                binding.inputProfilePasswordText.text = newEditable(it.password)

                binding.tvFullName.text = it.name
                binding.tvCircleName.text = Methods.splitNameProfile(it.name)

            }
        }
    }

    private fun initListeners() {
        with(binding) {
            ivEditTitular.setOnClickListener {
                it.findNavController().navigate(R.id.action_profileFragment_to_updateNameFragment)
            }

            ivEditEmail.setOnClickListener {
                it.findNavController().navigate(R.id.action_profileFragment_to_updateEmailFragment)
            }

            ivEditPassword.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_profileFragment_to_updatePasswordFragment)
            }

            ivEditPhone.setOnClickListener {
                it.findNavController().navigate(R.id.action_profileFragment_to_updatePhoneFragment)
            }

            btnLogout.setOnClickListener { profileViewModel.onLogoutSelected() }

            btnDeleteAccount.setOnClickListener { profileViewModel.onDeleteAccountSelected() }
        }

    }

    private fun initObservers() {
        profileViewModel.showDialogLogout.observe(requireActivity()) {
            if (it) showQuestionDialog(
                getString(R.string.dialog_error_sure),
                getString(R.string.dialog_logout_help),
                QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                    profileViewModel.logout()
                    toast(getString(R.string.dialog_closed_session))
                }
            )
        }

        profileViewModel.navigateToLogin.observe(requireActivity()) { goToLogin() }

        profileViewModel.showDialogDeleteAccount.observe(requireActivity()) {
            if (it) showQuestionDialog(
                getString(R.string.dialog_delete_account_sure),
                getString(R.string.dialog_delete_account_help),
                QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                    globalViewModel.getBankIban().observe(requireActivity()) { iban ->
                        profileViewModel.deleteAccountFromDB(iban)
                        toast(getString(R.string.dialog_deleted_account))
                    }
                }
            )
        }

        profileViewModel.navigateToWelcome.observe(requireActivity()) { goToWelcome() }
    }

    private fun showQuestionDialog(
        description: String,
        textHelp: String,
        positiveAction: QuestionDialog.Action
    ) {
        QuestionDialog.create(
            title = getString(R.string.dialog_error_oops),
            description = description,
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast(textHelp)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
            },
            positiveAction = positiveAction
        ).show(dialogLauncher, requireActivity())
    }
    private fun changeImage(){
        val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                it?.data?.data?.let {
                    currentFile = it
                    showQuestionDialogImage()
                    binding.ivCircle.setImageURI(it)
                }
            }else{
                Log.i("Ok","Ok")
            }
        }
        binding.ivCircle.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                imageLauncher.launch(it)
            }
        }
    }

    private fun showQuestionDialogImage() {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.defaultprofile)
        QuestionDialog.create(
            title = getString(R.string.dialog_error_oops),
            description = "Estas seguro de que quieres esta foto para tu fondo de perfil?",
            helpAction = QuestionDialog.Action(getString(R.string.dialog_error_help)) {
                toast("Vas a cambiar la foto de perfil de tu cuenta", Toast.LENGTH_LONG)
            },
            negativeAction = QuestionDialog.Action(getString(R.string.dialog_error_no)) {
                it.dismiss()
                binding.ivCircle.setImageDrawable(drawable)
            },
            positiveAction = QuestionDialog.Action(getString(R.string.dialog_error_yes)) {
                uploadImageToStorage("1")
                it.dismiss()
            }
        ).show(dialogLauncher, requireActivity())
    }

    private fun uploadImageToStorage(fileName : String){
        try {
            currentFile?.let {
                imageReference.child("images/$fileName").putFile(it).addOnSuccessListener{
                    toast("Succes upload image")
                }.addOnFailureListener{
                    toast("error upload image")
                }
            }
        }catch (e: Exception){
            toast("$e")
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(requireContext()))
    }

    private fun goToWelcome() {
        startActivity(WelcomeActivity.create(requireContext()))
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_profile)
    }
}