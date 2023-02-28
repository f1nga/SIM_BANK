package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentUpdateEmailBinding
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.profile_tab.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateEmailFragment : Fragment() {

    private lateinit var binding: FragmentUpdateEmailBinding
    private val globalViewModel: GlobalViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false)

       // initListeners()

        return binding.root
    }

    private fun initListeners() {
        binding.btnChange.setOnClickListener() {
            val newEmail = binding.inputNewEmailText.text.toString()

            globalViewModel.getUserFromDB().observe(requireActivity()) { currentUser ->
                val newUser = User(newEmail, currentUser.password , currentUser.name, currentUser.phone)
                globalViewModel.getBankIban().observe(requireActivity()) { iban ->
                    profileViewModel.updateEmailFromDB(newUser, iban)
                }
            }

            it.findNavController().navigate(R.id.action_updateEmailFragment_to_profileFragment)
        }
    }

}