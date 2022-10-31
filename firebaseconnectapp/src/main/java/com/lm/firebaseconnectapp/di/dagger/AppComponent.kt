package com.lm.firebaseconnectapp.di.dagger

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.MainActivity
import com.lm.firebaseconnectapp.data.FBMessageService
import com.lm.firebaseconnectapp.data.FirebaseMessageServiceCallback
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@[Component(modules = [MapAppModules::class]) Singleton]
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun context(application: Application): Builder

        @BindsInstance
        fun pendingIntentBuilder(pendingIntentBuilder: (Int, Intent) -> PendingIntent): Builder

        @BindsInstance
        fun intentBuilder(pendingIntentBuilder: (String, String) -> Intent): Builder

        @BindsInstance
        fun notificationBuilder(notificationBuilder: (String) -> NotificationCompat.Builder): Builder

        fun create(): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(fBMessageService: FBMessageService)
    fun firebaseMessageServiceCallback(): FirebaseMessageServiceCallback
    fun firebaseConnect(): FirebaseConnect
    @Named("Ringtone")
    fun ringtone(): Ringtone
    fun notificationManager(): NotificationManagerCompat
}