package com.lm.firebaseconnectapp.di.dagger.modules

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FBAuthInstanceModule {

    @Provides
    @Singleton
    fun providesFBAuthInstance() = FirebaseAuth.getInstance()
}