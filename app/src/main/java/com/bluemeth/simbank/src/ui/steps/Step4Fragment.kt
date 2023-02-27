package com.bluemeth.simbank.src.ui.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.databinding.FragmentStep4Binding

class Step4Fragment : Fragment() {
    private lateinit var binding: FragmentStep4Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStep4Binding.inflate(inflater,container,false)

        binding.btnAdelante.setOnClickListener(){

        }
        return binding.root
    }
}