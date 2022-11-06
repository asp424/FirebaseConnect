package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.ui.UiInteractor
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface UiInteractorModule {

    @[Binds Singleton]
    fun bindUiInteractor(uiInteractor: UiInteractor.Base): UiInteractor
}