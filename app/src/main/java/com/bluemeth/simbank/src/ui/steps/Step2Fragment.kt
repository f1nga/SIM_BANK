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


        viewGone()
        changeVisibility()


        return binding.root
    }

    fun viewGone(){
        binding.tvIban.setVisibility(View.GONE)
        binding.generateIban.setVisibility(View.GONE)
        binding.buttonGenerateMoney.setVisibility(View.GONE)
        binding.inputAlias.setVisibility(View.GONE)
    }
    fun changeVisibility(){

        binding.tvGenerarIBAN.setOnClickListener() {
            if (binding.tvIban.visibility == 0) {
                binding.tvIban.setVisibility(View.GONE)
                binding.generateIban.setVisibility(View.GONE)
            } else {
                binding.tvIban.setVisibility(View.VISIBLE)
                binding.generateIban.setVisibility(View.VISIBLE)
            }
        }

        binding.tvGenerarDinero.setOnClickListener(){
            if(binding.buttonGenerateMoney.visibility == 0){
                binding.buttonGenerateMoney.setVisibility(View.GONE)
            }else{
                binding.buttonGenerateMoney.setVisibility(View.VISIBLE)
            }
        }

        binding.tvAlias.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.setVisibility(View.GONE)
            }else{
                binding.inputAlias.setVisibility(View.VISIBLE)
            }
        }

        binding.imageView2.setOnClickListener(){
            if(binding.tvIban.visibility == 0){
                binding.tvIban.setVisibility(View.GONE)
                binding.generateIban.setVisibility(View.GONE)
            }else{
                binding.tvIban.setVisibility(View.VISIBLE)
                binding.generateIban.setVisibility(View.VISIBLE)
            }
        }

        binding.imageView3.setOnClickListener(){
            if(binding.buttonGenerateMoney.visibility == 0){
                binding.buttonGenerateMoney.setVisibility(View.GONE)
            }else{
                binding.buttonGenerateMoney.setVisibility(View.VISIBLE)
            }
        }


        binding.imageView4.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.setVisibility(View.GONE)
            }else{
                binding.inputAlias.setVisibility(View.VISIBLE)
            }
        }
    }

}