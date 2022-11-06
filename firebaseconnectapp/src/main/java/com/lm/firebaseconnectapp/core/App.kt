package com.lm.firebaseconnectapp.core

import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnectapp.di.dagger.DaggerAppComponent

class App : Application() {
    val appComponent by lazy {
        DaggerAppComponent.builder().context(this).pendingIntentBuilder { c, i ->
            getBroadcast(this, c, i,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }.notificationBuilder {
            NotificationCompat.Builder(this, it)
        }.intentBuilder { actions, bundle ->
            Intent(this, NotificationReceiver::class.java).putExtras(bundle)
                .apply { action = actions }
        }.create()
    }
}

