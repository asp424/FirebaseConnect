package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnectapp.notifications.Notifications
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class NotificationsModule {

    @[Provides Singleton]
    fun providesNotifications(
        notificationManager: NotificationManagerCompat,
        @Named("broad") pendingIntentBroadcastBuilder:
        Function2<@JvmSuppressWildcards Int, @JvmSuppressWildcards Intent, PendingIntent>,
        notificationBuilder: Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards NotificationCompat.Builder>,
        intentBuilder:
        Function2<@JvmSuppressWildcards String, @JvmSuppressWildcards Bundle, @JvmSuppressWildcards Intent>,
        context: Application
    ) = Notifications(
        notificationManager, pendingIntentBroadcastBuilder,
        notificationBuilder, intentBuilder, context
    )
}