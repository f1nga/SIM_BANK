package com.bluemeth.simbank.src.ui.home.tabs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentCardBinding
import com.bluemeth.simbank.src.data.adapters.CardRVAdapter
import com.bluemeth.simbank.src.data.adapters.RecyclerClickListener
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.UserInitData
import com.bluemeth.simbank.src.data.providers.UserInitData.Companion.random
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import com.bluemeth.simbank.src.data.providers.firebase.FirebaseClient
import com.bluemeth.simbank.src.ui.viewmodels.CreditCardViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.sql.Time
import java.util.*


class CardFragment() : Fragment() {

    private lateinit var binding: FragmentCardBinding
    private lateinit var adapter: CardRVAdapter
    private val creditCardViewModel: CreditCardViewModel by activityViewModels()
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_card,container,false);
        setRecyclerView()
        observeCard()

        binding.floatingActionButton.setOnClickListener(){
            var creditCardNumber = ""

            for (i in 1..16) {
                creditCardNumber += (0..9).random()
            }

            val money = (1000..3000).random().toDouble()
            val pin = (1000..9999).random()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))


            var credit = CreditCard(creditCardNumber, money, pin, cvv, caducityTime, "ES3371743112497932600524")
            db.collection("credit_cards").document(creditCardNumber).set(credit)
            observeCard()
        }

        setHasOptionsMenu(true)
        return binding.root


    }

    private fun setRecyclerView() {

        val cardRecyclerview = binding.recyclerViewCards
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        cardRecyclerview.setHasFixedSize(true)
        adapter = CardRVAdapter(requireContext())
        cardRecyclerview.adapter = adapter
        adapter.setItemListener(object : RecyclerClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(),"Cabolo" , Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun observeCard() {
        creditCardViewModel.fetchCardData().observe(requireActivity(), Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }


}