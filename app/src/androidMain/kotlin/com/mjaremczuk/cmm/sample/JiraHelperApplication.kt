package com.mjaremczuk.cmm.sample

import android.app.Application
import androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JiraHelperApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@JiraHelperApplication)
            modules(androidModule)
        }
    }
}