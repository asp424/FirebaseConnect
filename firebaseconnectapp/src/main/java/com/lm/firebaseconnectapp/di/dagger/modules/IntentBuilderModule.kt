package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.lm.firebaseconnectapp.notifications.NotificationReceiver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class IntentBuilderModule {

    @Provides
    @Singleton
    fun provideIntentBuilder(context: Application):
            Function2<@JvmSuppressWildcards String, @JvmSuppressWildcards Bundle, @JvmSuppressWildcards Intent> =
        { actions, bundle ->
            Intent(context, NotificationReceiver::class.java).putExtras(bundle)
                .apply { action = actions }
        }
}