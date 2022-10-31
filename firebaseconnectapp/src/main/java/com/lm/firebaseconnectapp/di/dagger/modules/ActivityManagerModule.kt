package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ActivityManagerModule {

    @[Provides Singleton]
    fun providesActivityManager(context: Application) =
        context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
}