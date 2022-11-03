package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.data.SPreferences
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SPreferencesModule {

    @Binds
    @Singleton
    fun bindsSPreferences(sPreferences: SPreferences.Base): SPreferences
}