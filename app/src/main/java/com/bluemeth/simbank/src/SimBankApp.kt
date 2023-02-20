package com.bluemeth.simbank.src

import android.app.Application
import com.bluemeth.simbank.BuildConfig
import com.bluemeth.simbank.src.data.providers.Prefs
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class SimBankApp : Application() {

    companion object {
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}