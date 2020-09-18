package com.example.nabrea.itemizeapp

import android.app.Application
import timber.log.Timber

class ItemizeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}