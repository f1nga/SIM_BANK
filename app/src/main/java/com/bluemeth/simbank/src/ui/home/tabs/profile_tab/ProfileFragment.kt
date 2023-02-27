package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentProfileBinding
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.HomeViewModel
import com.bluemeth.simbank.src.utils.GlobalVariables
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setNames()
        setPersonalData()
        initListeners()

        return binding.root
    }

    private fun editUserName(name: String) {
        profileViewModel.updateNameFromDB(GlobalVariables.userEmail!!, name)
        setNames()
    }

    private fun setNames() {
        globalViewModel.getUserName().observe(requireActivity()) {
            binding.tvFullName.text = it
            binding.tvCircleName.text = Methods.splitNameProfile(it)
        }
    }

    private fun setPersonalData() {
        val inputText = Editable.Factory.getInstance()
        globalViewModel.getUserFromDB().observe(requireActivity()) {
            binding.inputProfileEmailText.text = inputText.newEditable(it.email)
            binding.inputProfilePhoneText.text = inputText.newEditable(it.phone.toString())
        }

    }

    private fun initListeners() {
        binding.ivEdit.setOnClickListener {
            editUserName(binding.inputEditNameText.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_profile)
    }

}