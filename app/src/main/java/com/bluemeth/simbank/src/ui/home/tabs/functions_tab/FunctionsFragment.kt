package com.bluemeth.simbank.src.ui.home.tabs.functions_tab

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentFunctionsBinding
import com.bluemeth.simbank.src.ui.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FunctionsFragment : Fragment() {

    private lateinit var binding: FragmentFunctionsBinding
    private val globalViewModel: GlobalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFunctionsBinding.inflate(inflater,container,false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.clTransferencia.setOnClickListener { goToTransferFunction() }

        binding.clBizum.setOnClickListener { goToBizumFunction() }

        binding.clCuenta.setOnClickListener { goToAccountFunction() }

        binding.clPromociones.setOnClickListener { goToPromotionsFunction() }

        binding.cvMoreCards.setOnClickListener { goToAddCards() }
    }

    private fun goToTransferFunction() {
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_transferFragment)
    }

    private fun goToBizumFunction() {
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_bizumFragment)
    }

    private fun goToAccountFunction() {
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_infoAccountFragment)
    }

    private fun goToPromotionsFunction(){
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_promotionsFragment)
    }

    private fun goToAddCards(){
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_addCreditCardFragment)
    }

    override fun onStart() {
        super.onStart()

        val tvTitle = requireActivity().findViewById<TextView>(R.id.tvNameBar)
        tvTitle.text = getString(R.string.toolbar_functions)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_functionsFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }
}