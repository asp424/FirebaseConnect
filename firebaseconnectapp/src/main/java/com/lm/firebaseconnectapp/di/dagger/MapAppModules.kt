package com.lm.firebaseconnectapp.di.dagger

import com.lm.firebaseconnectapp.di.dagger.modules.*
import dagger.Module

@Module(
    includes = [
        FirebaseConnectModule::class,
        ActivityManagerModule::class,
        FirebaseMessageServiceCallbackModule::class,
        NotificationManagerModule::class,
        NotificationsModule::class,
        RingtoneModule::class,
        NotificationSoundModule::class,
        AppIsRunCheckerModule::class
    ]
)
interface MapAppModules