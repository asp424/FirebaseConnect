package com.lm.firebaseconnectapp.di.dagger

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.core.NotificationReceiver
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.data.FBMessageService
import com.lm.firebaseconnectapp.data.FirebaseMessageServiceCallback
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import com.lm.firebaseconnectapp.ui.UiInteractor
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
        fun intentBuilder(pendingIntentBuilder: (String, Bundle) -> Intent): Builder

        @BindsInstance
        fun notificationBuilder(notificationBuilder: (String) -> NotificationCompat.Builder): Builder

        fun create(): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(fBMessageService: FBMessageService)
    fun inject(notificationReceiver: NotificationReceiver)
    fun firebaseMessageServiceCallback(): FirebaseMessageServiceCallback
    fun fBConnect(): FirebaseConnect
    @Named("Ringtone")
    fun ringtone(): Ringtone
    fun notificationManager(): NotificationManagerCompat
    fun sPreferences(): SPreferences
    fun oneTapGoogleAuth(): OneTapGoogleAuth
    fun firebaseAuth(): FirebaseAuth
}