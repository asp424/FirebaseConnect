package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.data.one_tap_google.FBAuth
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface FBAuthModule {

    @Binds
    @Singleton
    fun bindsFBAuth(fbAuth: FBAuth.Base): FBAuth
}