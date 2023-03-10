package com.bluemeth.simbank.src.ui.drawer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentPrivacyPolicyBinding
import com.bluemeth.simbank.databinding.FragmentStep1Binding

class PrivacyPolicyFragment : Fragment() {
    private lateinit var binding: FragmentPrivacyPolicyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentPrivacyPolicyBinding.inflate(inflater,container,false)

        return binding.root
    }

}