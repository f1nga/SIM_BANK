package com.bluemeth.simbank.src.core.ex

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.setActivityTitle(title: String) {
    (activity as AppCompatActivity?)?.supportActionBar?.title = title
}
