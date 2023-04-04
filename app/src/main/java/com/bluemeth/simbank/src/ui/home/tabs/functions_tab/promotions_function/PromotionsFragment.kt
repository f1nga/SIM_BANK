package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluemeth.simbank.R
import com.bluemeth.simbank.databinding.FragmentPromotionsBinding
import com.bluemeth.simbank.src.core.dialog.DialogFragmentLauncher
import com.bluemeth.simbank.src.core.dialog.ParticipateDialog
import com.bluemeth.simbank.src.core.ex.show
import com.bluemeth.simbank.src.data.providers.PromotionsFeaturesProvider
import com.bluemeth.simbank.src.data.providers.PromotionsHeaderProvider
import com.bluemeth.simbank.src.ui.GlobalViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsFeatures
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsHeader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PromotionsFragment : Fragment() {

    private lateinit var binding: FragmentPromotionsBinding
    private val globalViewModel: GlobalViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromotionsBinding.inflate(inflater,container,false)

        initUI()
        return binding.root
    }

    private fun initUI(){
        setRecyclerViewHeader()
        setRecyclerViewFeatures()
        participate()
    }


    private fun participate(){
        binding.btnParticipate.setOnClickListener{
            showParticipateDialog()
        }
    }

    private fun setRecyclerViewHeader() {

        val cardRecyclerview = binding.rvInfoCircles
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        cardRecyclerview.setHasFixedSize(true)

        val cardAdapter = PromotionsHeaderRVAdapter()
        cardRecyclerview.adapter = cardAdapter
        cardAdapter.setListData(PromotionsHeaderProvider.getListInfoCard())

        cardAdapter.setItemListener(object : PromotionsHeaderRVAdapter.OnItemClickListener {
            override fun onItemClick(promotionsHeader: PromotionsHeader) {

            }
        })
    }

    private fun setRecyclerViewFeatures() {

        val cardRecyclerview = binding.rvDestacados
        cardRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        cardRecyclerview.setHasFixedSize(true)

        val cardAdapter = PromotionsFeaturesRVAdapter()
        cardRecyclerview.adapter = cardAdapter
        cardAdapter.setListData(PromotionsFeaturesProvider.getListInfoCard())

        cardAdapter.setItemListener(object : PromotionsFeaturesRVAdapter.OnItemClickListener {
            override fun onItemClick(promotionsFeatures: PromotionsFeatures) {

            }
        })
    }

    private fun showParticipateDialog() {
        ParticipateDialog.create(
            ParticipateDialog.Action(getString(R.string.dialog_verified_positive)) {  }
        ).show(dialogLauncher, requireActivity())
    }

    override fun onStart() {
        super.onStart()
        val tvTitle = requireActivity().findViewById<View>(R.id.tvNameBar) as TextView

        tvTitle.text = getString(R.string.toolbar_promotions)

        requireActivity().findViewById<ImageView>(R.id.ivNotifications).let {
            it.setOnClickListener { view?.findNavController()?.navigate(R.id.action_promotionsFragment_to_notificationsFragment) }

            globalViewModel.isEveryNotificationReadedFromDB(globalViewModel.getUserAuth().email!!).observe(requireActivity()) {isReaded ->
                it.setImageResource(if (isReaded) R.drawable.ic_notifications else R.drawable.ic_notifications_red)
            }
        }
    }

}