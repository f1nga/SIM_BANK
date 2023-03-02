package com.bluemeth.simbank.src.ui.steps.step1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.databinding.FragmentStep1Binding

class Step1Fragment : Fragment() {
    private lateinit var binding: FragmentStep1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStep1Binding.inflate(inflater,container,false)

        binding.viewPlaceholder.setOnClickListener(){
            Log.i("hol","hol")
        }
        return binding.root
    }


}