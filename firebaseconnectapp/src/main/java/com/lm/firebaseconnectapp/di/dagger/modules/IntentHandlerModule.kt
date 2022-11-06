package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.core.IntentHandler
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface IntentHandlerModule {

    @[Binds Singleton]
    fun bindIntentHandler(intentHandler: IntentHandler.Base): IntentHandler
}