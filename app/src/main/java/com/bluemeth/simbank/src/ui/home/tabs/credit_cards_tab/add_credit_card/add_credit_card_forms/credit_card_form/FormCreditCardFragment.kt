package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.credit_card_form

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddCreditCardBinding
import com.bluemeth.simbank.databinding.FragmentFormCreditCardBinding
import com.bluemeth.simbank.databinding.FragmentSettingsBinding
import com.bluemeth.simbank.src.ui.steps.step2.model.Step2Model
import com.bluemeth.simbank.src.utils.Methods
import kotlinx.android.synthetic.main.fragment_form_credit_card.*


class FormCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentFormCreditCardBinding
    private var moneyBank: Double? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFormCreditCardBinding.inflate(inflater,container,false)

        binding.buttonGenerateMoney.setOnClickListener {
            binding.viewPlaceholder.visibility = View.VISIBLE
            binding.buttonGenerateMoney.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                viewPlaceholder.isVisible = false
                binding.buttonGenerateMoney.isVisible = false

                moneyBank = Methods.roundOffDecimal(Methods.generateMoneyCreditCard())
                binding.tvDinero.text = Methods.formatMoney(moneyBank!!)
                binding.tvDinero.isVisible = true
            }, 2700)

        }

        return binding.root
    }

}