package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class PendingIntentBroadcastBuilderModule {

    @Singleton
    @Provides
    @Named("broad")
    fun providePendingIntentBroadcastBuilder(context: Application):
            Function2<@JvmSuppressWildcards Int, @JvmSuppressWildcards Intent, PendingIntent> =
        { c, i ->
            PendingIntent.getBroadcast(
                context, c, i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
}