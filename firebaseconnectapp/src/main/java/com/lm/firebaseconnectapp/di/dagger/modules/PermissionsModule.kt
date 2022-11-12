package com.lm.firebaseconnectapp.di.dagger.modules

import com.lm.firebaseconnectapp.core.Permissions
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface PermissionsModule {
    @Binds
    @Singleton
    fun bindsPermissions(permissions: Permissions.Base): Permissions
}