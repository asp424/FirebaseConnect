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
        AppIsRunCheckerModule::class,
        SPreferencesModule::class,
        SharedPreferencesModule::class,
        FBAuthInstanceModule::class,
        OneTapGoogleAuthModule::class,
        FBAuthModule::class,
        FBAuthInstanceModule::class,
        IntentHandlerModule::class,
        UiInteractorModule::class,
        NotificationReceiverModule::class
    ]
)
interface MapAppModules