package com.lm.firebaseconnectapp.di.dagger

import android.app.Application
import android.content.BroadcastReceiver
import com.lm.firebaseconnectapp.notifications.NotificationReceiver
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.service.FBMessageService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@[Component(modules = [MapAppModules::class]) Singleton]
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun context(application: Application): Builder

        fun create(): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(fBMessageService: FBMessageService)
    fun inject(notificationReceiver: NotificationReceiver)

    fun inject(broadcastReceiver: BroadcastReceiver)
}