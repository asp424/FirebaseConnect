package com.lm.firebaseconnectapp.di.dagger.modules

import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.UiStates
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UiStatesModule {

    @Provides
    @Singleton
    fun provideUiStates(sPreferences: SPreferences) = UiStates()
        .apply {
            Color(sPreferences.readMainColor()).setMainColor
            Color(sPreferences.readSecondColor()).setSecondColor
        }
}
