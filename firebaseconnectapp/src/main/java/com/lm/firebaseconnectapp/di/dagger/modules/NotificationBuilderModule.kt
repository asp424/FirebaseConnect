package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotificationBuilderModule {

    @Provides
    @Singleton
    fun provideNotificationBuilder(context: Application):
            Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards NotificationCompat.Builder> =
        { NotificationCompat.Builder(context, it) }
}