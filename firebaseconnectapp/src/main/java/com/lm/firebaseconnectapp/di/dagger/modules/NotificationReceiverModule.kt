package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.notifications.NotificationReceiver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotificationReceiverModule {

    @[Provides Singleton]
    fun provideNotificationReceiver() = NotificationReceiver()
}