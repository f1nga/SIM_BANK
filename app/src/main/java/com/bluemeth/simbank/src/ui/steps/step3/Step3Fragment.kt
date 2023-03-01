package com.bluemeth.simbank.src.ui.steps.step3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluemeth.simbank.databinding.FragmentStep3Binding
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step3Fragment : Fragment() {
    private lateinit var binding: FragmentStep3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStep3Binding.inflate(inflater,container,false)

        viewGone()
        changeVisibility()
        generateNumberCreditCard()
        return binding.root
    }

    fun generateNumberCreditCard(){
        binding.generateNumberCreditCard.setOnClickListener(){
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }
            binding.tvNumeroTarjeta.text = Methods.formatCardNumber(creditCardNumber)
        }
    }


    fun viewGone(){
        binding.tvNumeroTarjeta.visibility = View.GONE
        binding.generateNumberCreditCard.visibility = View.GONE
        binding.inputAlias.visibility = View.GONE
        binding.inputPin.visibility = View.GONE
    }
    fun changeVisibility(){

        binding.tvGenerarNumero.setOnClickListener() {
            if (binding.tvNumeroTarjeta.visibility == 0) {
                binding.tvNumeroTarjeta.visibility = View.GONE
                binding.generateNumberCreditCard.visibility = View.GONE
            } else {
                binding.tvNumeroTarjeta.visibility = View.VISIBLE
                binding.generateNumberCreditCard.visibility = View.VISIBLE
            }
        }

        binding.tvAliasCreditCard.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.visibility = View.GONE
            }else{
                binding.inputAlias.visibility = View.VISIBLE
            }
        }

        binding.tvPin.setOnClickListener(){
            if(binding.inputPin.visibility == 0){
                binding.inputPin.visibility = View.GONE
            }else{
                binding.inputPin.visibility = View.VISIBLE
            }
        }

        binding.imageView2.setOnClickListener(){
            if(binding.tvNumeroTarjeta.visibility == 0){
                binding.tvNumeroTarjeta.visibility = View.GONE
                binding.generateNumberCreditCard.visibility = View.GONE
            }else{
                binding.tvNumeroTarjeta.visibility = View.VISIBLE
                binding.generateNumberCreditCard.visibility = View.VISIBLE
            }
        }

        binding.imageView3.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.visibility = View.GONE
            }else{
                binding.inputAlias.visibility = View.VISIBLE
            }
        }


        binding.imageView4.setOnClickListener(){
            if(binding.inputPin.visibility == 0){
                binding.inputPin.visibility = View.GONE
            }else{
                binding.inputPin.visibility = View.VISIBLE
            }
        }
    }

}