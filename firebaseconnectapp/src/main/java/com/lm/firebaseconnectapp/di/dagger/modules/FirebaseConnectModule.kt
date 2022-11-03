package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.BuildConfig.C_KEY
import com.lm.firebaseconnectapp.BuildConfig.FCM_SERVER_KEY
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseConnectModule {

    @[Singleton Provides]
    fun provideFirebaseConnectModule()
    = FirebaseConnect.Instance(C_KEY, FCM_SERVER_KEY).init()
}