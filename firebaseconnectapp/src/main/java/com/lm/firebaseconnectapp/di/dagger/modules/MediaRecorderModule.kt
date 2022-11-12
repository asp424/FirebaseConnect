package com.lm.firebaseconnectapp.di.dagger.modules

import android.media.MediaRecorder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MediaRecorderModule {

    @[Singleton Provides]
    fun provideMediaRecorder():
            Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards MediaRecorder?> = {
        MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(it)

            }
    }
}