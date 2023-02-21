package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.databinding.FragmentCreditCardBinding
import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.data.models.CreditCard
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreditCardFragment() : Fragment() {

    private lateinit var binding: FragmentCreditCardBinding
    private val creditCardViewModel: CreditCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditCardBinding.inflate(inflater,container,false)
        setRecyclerView()
        observeCard()

        binding.floatingActionButton.setOnClickListener(){
           // it.findNavController().navigate(R.id.action_cardFragment_to_addCreditCardFragment)
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }

            val money = (1000..3000).random().toDouble()
            val pin = (1000..9999).random()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))

            creditCardViewModel.insertCreditCardToDB(
                CreditCard(
                    creditCardNumber,
                    money,
                    pin,
                    cvv,
                    caducityTime,
                    prefs.getIban()
                )
            )
            observeCard()

        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.recyclerViewCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        cardRecyclerview.adapter = creditCardViewModel.cardAdapter

        creditCardViewModel.cardAdapter.setItemListener(object : RecyclerClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(),"Cabolo" , Toast.LENGTH_SHORT).show()
            }
        })

        creditCardViewModel.getNameUserCard(prefs.getEmail()).observe(requireActivity()) {
            creditCardViewModel.cardAdapter.setUserName(it.name)
        }

    }

    private fun observeCard() {
        creditCardViewModel.fetchCardData(prefs.getIban()).observe(requireActivity()) { creditCardList ->
            creditCardViewModel.cardAdapter.setListData(creditCardList)
            creditCardViewModel.cardAdapter.notifyDataSetChanged()
        }

    }



}