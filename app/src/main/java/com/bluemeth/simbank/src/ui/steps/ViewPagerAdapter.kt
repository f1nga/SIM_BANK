package com.bluemeth.simbank.src.ui.steps

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle ): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        // Cantidad de fragments
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> Step1Fragment()
            1 -> Step2Fragment()
            2 -> Step3Fragment()
            else -> Step1Fragment()
        }
    }

}