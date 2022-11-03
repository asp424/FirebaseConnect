package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface OneTapGoogleAuthModule {

    @Binds
    @Singleton
    fun bindsOneTapGoogleAuth(oneTapGoogleAuth: OneTapGoogleAuth.Base): OneTapGoogleAuth
}