package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentCreditCardBinding
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.utils.GlobalVariables
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditCardFragment() : Fragment() {

    private lateinit var binding: FragmentCreditCardBinding
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditCardBinding.inflate(inflater,container,false)

        setRecyclerView()
        observeCard()

        binding.floatingActionButton.setOnClickListener(){
            view?.findNavController()?.navigate(R.id.action_cardFragment_to_addCreditCardFragment)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.recyclerViewCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = creditCardViewModel.cardAdapter
        creditCardViewModel.cardAdapter.setItemListener(object: CreditCardRVAdapter.OnItemClickListener {

            override fun onItemClick(creditCard: CreditCard) {
                creditCardViewModel.setCard(creditCard)
                view?.findNavController()?.navigate(R.id.action_cardFragment_to_infoCardFragment)
            }
        })

        creditCardViewModel.getNameUserCard(GlobalVariables.userEmail!!).observe(requireActivity()) {
            creditCardViewModel.cardAdapter.setUserName(it.name)
        }

    }

    private fun observeCard() {
        binding.progressBar.visibility = View.VISIBLE
        creditCardViewModel.getBankAccount(GlobalVariables.userEmail!!).observe(requireActivity()) {
            creditCardViewModel.getCreditsCardsFromDB(it.iban).observe(requireActivity()) { creditCardList ->
                creditCardViewModel.cardAdapter.setListData(creditCardList)
                creditCardViewModel.cardAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_cards)
    }
}