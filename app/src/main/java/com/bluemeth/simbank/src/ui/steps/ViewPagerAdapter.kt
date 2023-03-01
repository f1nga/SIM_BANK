package com.bluemeth.simbank.src.ui.steps

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bluemeth.simbank.src.ui.steps.step1.Step1Fragment
import com.bluemeth.simbank.src.ui.steps.step2.Step2Fragment
import com.bluemeth.simbank.src.ui.steps.step3.Step3Fragment
import com.bluemeth.simbank.src.ui.steps.step4.Step4Fragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle ): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        // Cantidad de fragments
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> Step1Fragment()
            1 -> Step2Fragment()
            2 -> Step3Fragment()
            3 -> Step4Fragment()
            else -> Step1Fragment()
        }
    }

}