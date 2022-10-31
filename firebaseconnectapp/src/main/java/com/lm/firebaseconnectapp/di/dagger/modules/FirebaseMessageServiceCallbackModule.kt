package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.ActivityManager
import android.app.Application
import android.media.Ringtone
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.core.Notifications
import com.lm.firebaseconnectapp.data.FirebaseMessageServiceCallback
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class FirebaseMessageServiceCallbackModule {

    @[Provides Singleton]
    fun provideFirebaseMessageServiceCallback(
        context: Application, firebaseConnect: FirebaseConnect, activityManager: ActivityManager,
        notifications: Notifications,
        @Named("Ringtone") ringtone: Ringtone,
        @Named("Notify") notificationSound: Ringtone
    ) = FirebaseMessageServiceCallback(
        context, firebaseConnect, activityManager, notifications, ringtone, notificationSound
    )
}