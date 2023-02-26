package com.bluemeth.simbank.src.ui.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentStep1Binding
import com.bluemeth.simbank.databinding.FragmentStep2Binding

class Step2Fragment : Fragment() {
    private lateinit var binding: FragmentStep2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStep2Binding.inflate(inflater,container,false)

        return binding.root
    }

}