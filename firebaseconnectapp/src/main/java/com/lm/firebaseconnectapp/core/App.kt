package com.lm.firebaseconnectapp.core

import android.app.Application
import com.lm.firebaseconnectapp.di.dagger.DaggerAppComponent

class App : Application() {
    val appComponent by lazy {
            DaggerAppComponent.builder().context(this).create()
    }
}

