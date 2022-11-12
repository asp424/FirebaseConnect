package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.media.Ringtone
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.notifications.Notifications
import com.lm.firebaseconnectapp.service.FirebaseMessageServiceCallback
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class FirebaseMessageServiceCallbackModule {

    @[Provides Singleton]
    fun provideFirebaseMessageServiceCallback(
        context: Application,
        firebaseConnect: FirebaseConnect,
        notificationManager: NotificationManagerCompat,
        notifications: Notifications,
        @Named("Ringtone") ringtone: Ringtone,
        @Named("Notify") notificationSound: Ringtone,
        @Named("isRun")
        appIsRunChecker: () -> Boolean
    ) = FirebaseMessageServiceCallback(
        context, firebaseConnect,
        notifications,
        notificationManager,
        ringtone,
        notificationSound,
        appIsRunChecker
    )
}