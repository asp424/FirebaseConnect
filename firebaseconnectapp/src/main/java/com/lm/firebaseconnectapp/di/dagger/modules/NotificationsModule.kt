package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnectapp.core.Notifications
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotificationsModule {

    @[Provides Singleton]
    fun providesNotifications(
        notificationManager: NotificationManagerCompat,
        pendingIntentBuilder: (Int, Intent) -> PendingIntent,
        notificationBuilder: (String) -> NotificationCompat.Builder,
        intentBuilder: (String, Bundle) -> Intent
    ) = Notifications(
        notificationManager, pendingIntentBuilder, notificationBuilder, intentBuilder
    )
}