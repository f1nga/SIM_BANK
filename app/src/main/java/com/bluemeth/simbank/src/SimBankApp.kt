package com.bluemeth.simbank.src

import android.app.Application
import com.bluemeth.simbank.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class SimBankApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}