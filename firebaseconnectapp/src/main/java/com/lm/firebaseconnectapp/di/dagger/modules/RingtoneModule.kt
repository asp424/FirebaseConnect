package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RingtoneModule {

    @RequiresApi(Build.VERSION_CODES.P)
    @[Singleton Provides Named("Ringtone")]
    fun provideRingtone(context: Application): Ringtone =
        RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ).apply { isLooping = true }
}