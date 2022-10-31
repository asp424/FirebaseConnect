package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NotificationManagerModule {

    @[Provides Singleton]
    fun provideNotificationManager(context: Application) = NotificationManagerCompat.from(context)
}