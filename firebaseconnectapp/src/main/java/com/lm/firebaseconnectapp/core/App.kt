package com.lm.firebaseconnectapp.core

import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lm.firebaseconnectapp.di.dagger.DaggerAppComponent

class App : Application() {
    val appComponent by lazy {
        DaggerAppComponent.builder().context(this).pendingIntentBuilder { c, i ->
            PendingIntent.getBroadcast(this, c, i, FLAG_IMMUTABLE)
        }.notificationBuilder {
            NotificationCompat.Builder(this, it)
        }.intentBuilder { actions, extras ->
            Intent(this, NotificationReceiver::class.java).apply {
                action = actions
                putExtra("callingId", extras)
            }
        }.create()
    }
}

