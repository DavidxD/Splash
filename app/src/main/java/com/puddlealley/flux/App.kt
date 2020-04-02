package com.puddlealley.flux

import android.app.Application
import com.puddlealley.flux.store.AppStore
import com.puddlealley.splash.android.connect
import org.koin.android.ext.android.inject
import timber.log.Timber

class App : Application() {

    val appStore: AppStore by inject()

    override fun onCreate() {
        super.onCreate()
        createKoin(this)

        Timber.plant(Timber.DebugTree())
    }
}