package com.bluemeth.simbank.src.ui.steps.step2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentStep2Binding
import com.bluemeth.simbank.src.core.ex.loseFocusAfterAction
import com.bluemeth.simbank.src.core.ex.onTextChanged
import com.bluemeth.simbank.src.ui.steps.step3.Step3ViewState
import com.bluemeth.simbank.src.ui.steps.step3.model.Step3Model
import com.bluemeth.simbank.src.utils.Methods
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step2Fragment : Fragment() {
    private lateinit var binding: FragmentStep2Binding
    private val step2ViewModel: Step2ViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentStep2Binding.inflate(inflater,container,false)

        initUI()
        return binding.root
    }

    private fun initUI() {
        viewGone()
        changeVisibility()
        initListeners()
        initObservers()
    }

    fun generateMoney(){
            val money = (10000..20000).random().toDouble()
            binding.buttonGenerateMoney.setVisibility(View.GONE)
            binding.tvDinero.text = Methods.formatMoney(money)
            binding.tvDinero.setVisibility(View.VISIBLE)

    }

    fun generateIBAN(){
            var bankNumber = "ES33"

            for (i in 1..20) {
                bankNumber += (0..9).random()
            }
            binding.tvIban.text = bankNumber
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            step2ViewModel.onFieldsChanged(
                    binding.inputAliasText.text.toString(),
            )
        }
    }

    private fun initListeners() {

        binding.inputAliasText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.inputAliasText.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.inputAliasText.onTextChanged { onFieldChanged() }



        binding.generateIban.setOnClickListener {
            generateIBAN()
            step2ViewModel.isValidGeneratedIbanButtonClicked(
                    alias = binding.inputAliasText.text.toString(),
            )
        }

        binding.buttonGenerateMoney.setOnClickListener {
            binding.viewPlaceholder.visibility = View.VISIBLE
            binding.buttonGenerateMoney.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.viewPlaceholder.visibility = View.GONE
                generateMoney()
            }, 2700)

            step2ViewModel.isValidGeneratedMoneyButtonClicked(
                alias = binding.inputAliasText.text.toString(),
            )
        }

    }
    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            step2ViewModel.viewState.collect { viewState ->
                updateUI(viewState)

                val btnNext = requireActivity().findViewById<Button>(R.id.nextButton)
                btnNext.isVisible = viewState.isStep2Validated()

            }
        }
    }

    private fun updateUI(viewState: Step2ViewState) {
        binding.inputAlias.error =
            if (viewState.isValidAlias) null else getString(R.string.alias_not_correct)

    }



    fun viewGone(){
        binding.viewPlaceholder.visibility = View.GONE
        binding.tvIban.visibility = View.GONE
        binding.generateIban.visibility = View.GONE
        binding.buttonGenerateMoney.visibility = View.GONE
        binding.inputAlias.visibility = View.GONE
        binding.tvDinero.visibility = View.GONE
    }

    fun changeVisibility(){

        binding.tvGenerarIBAN.setOnClickListener() {
            if (binding.tvIban.visibility == 0) {
                binding.tvIban.visibility = View.GONE
                binding.generateIban.visibility = View.GONE
            } else {
                binding.tvIban.visibility = View.VISIBLE
                binding.generateIban.visibility = View.VISIBLE
            }
        }

        binding.tvGenerarDinero.setOnClickListener(){
            if(binding.buttonGenerateMoney.visibility == 0){
                binding.buttonGenerateMoney.visibility = View.GONE
            }else if(binding.buttonGenerateMoney.visibility == 8 && binding.tvDinero.visibility==0){
                binding.buttonGenerateMoney.visibility = View.GONE
            }else{
                binding.buttonGenerateMoney.visibility = View.VISIBLE
            }
        }

        binding.tvAlias.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.visibility = View.GONE
            }else{
                binding.inputAlias.visibility = View.VISIBLE
            }
        }

        binding.imageView2.setOnClickListener(){
            if(binding.tvIban.visibility == 0){
                binding.tvIban.visibility = View.GONE
                binding.generateIban.visibility = View.GONE
            }else{
                binding.tvIban.visibility = View.VISIBLE
                binding.generateIban.visibility = View.VISIBLE
            }
        }

        binding.imageView3.setOnClickListener(){
            if(binding.buttonGenerateMoney.visibility == 0){
                binding.buttonGenerateMoney.visibility = View.GONE
            }else if(binding.buttonGenerateMoney.visibility == 8 && binding.tvDinero.visibility==0){
                binding.buttonGenerateMoney.visibility = View.GONE
            }else{
                binding.buttonGenerateMoney.visibility = View.VISIBLE
            }
        }


        binding.imageView4.setOnClickListener(){
            if(binding.inputAlias.visibility == 0){
                binding.inputAlias.visibility = View.GONE
            }else{
                binding.inputAlias.visibility = View.VISIBLE
            }
        }
    }

}