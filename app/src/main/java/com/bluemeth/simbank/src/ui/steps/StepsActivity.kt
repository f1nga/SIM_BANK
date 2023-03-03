package com.bluemeth.simbank.src.ui.steps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bluemeth.simbank.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_steps.*

@AndroidEntryPoint
class StepsActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, StepsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        //log("numberr", Methods.splitEuro("18.600,00€").toString())
        setupStepView()
        setupViewPager()
        setupButtons()
    }

    private fun setupStepView(){
        stepView.state
            .steps(listOf("Introducción", "Crear Cuenta", "Crear Tarjeta","Estas listo")) // You should specify only steps number or steps array of strings.
            .stepsNumber(3)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            .commit()

        stepView.setOnStepClickListener { position ->
            viewPager.setCurrentItem(position, false)
        }

    }

    private fun setupViewPager(){
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback(){

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    stepView.go(position, true)
                    setButtons(position)

                }

            }
        )

    }

    private fun setupButtons(){
        backButton.setOnClickListener{
            viewPager.setCurrentItem(viewPager.currentItem-1, false)
        }

        nextButton.setOnClickListener{
            viewPager.setCurrentItem(viewPager.currentItem+1, false)
        }
    }

    private fun setButtons(position:Int){
        when(position){
            0 -> {
                backButton.visibility = View.INVISIBLE
                nextButton.visibility = View.VISIBLE
            }
            1->{
                backButton.visibility = View.INVISIBLE
                nextButton.visibility = View.INVISIBLE
            }
            2 -> {
                backButton.visibility = View.INVISIBLE
                nextButton.visibility = View.INVISIBLE
            }
            3 -> {
                backButton.visibility = View.INVISIBLE
                nextButton.visibility = View.INVISIBLE
            }
        }
    }
}