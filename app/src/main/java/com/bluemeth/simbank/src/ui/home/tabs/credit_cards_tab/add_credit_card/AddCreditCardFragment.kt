package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentAddCreditCardBinding
import com.bluemeth.simbank.src.core.ex.toast
import com.bluemeth.simbank.src.data.providers.InfoCardProvider
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.model.CreditCardInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCreditCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCreditCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCreditCardBinding.inflate(inflater,container,false)

        setRecyclerView()
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.addCardRecyclerView
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        cardRecyclerview.setHasFixedSize(true)

        val cardAdapter = AddCreditCardRVAdapter()
        cardRecyclerview.adapter = cardAdapter
        cardAdapter.setListData(InfoCardProvider.getListInfoCard())

        cardAdapter.setItemListener(object : AddCreditCardRVAdapter.OnItemClickListener {
            override fun onItemClick(creditCard: CreditCardInfo) {
                if(creditCard.cardImg == R.drawable.visacredito){
                    view?.findNavController()?.navigate(R.id.action_addCreditCardFragment_to_formCreditCardFragment)
                }else if(creditCard.cardImg == R.drawable.visadebito){
                    view?.findNavController()?.navigate(R.id.action_addCreditCardFragment_to_formDebidCardFragment)
                }else{
                    view?.findNavController()?.navigate(R.id.action_addCreditCardFragment_to_formPrepaidCardFragment)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_add_card)
    }

}