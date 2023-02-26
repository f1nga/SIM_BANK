package com.bluemeth.simbank.src.core.ex

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), text, length).show()
}

fun Fragment.log(tag: String, msg: String) {
    Log.i(tag, msg)
}
