package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.ActivityManager
import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppIsRunCheckerModule {

    @[Provides Singleton Named("isRun")]
    fun provideAppIsRunChecker(activityManager: ActivityManager, context: Application)
    : () -> Boolean = {
        with(activityManager.runningAppProcesses){
            any {
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && it.processName == context.packageName
            }
        }
    }
}