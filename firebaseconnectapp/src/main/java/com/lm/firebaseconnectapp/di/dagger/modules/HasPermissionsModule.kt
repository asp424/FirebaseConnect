package com.lm.firebaseconnectapp.di.dagger.modules

import android.app.Application
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import com.lm.firebaseconnectapp.core.Permissions.Base.Companion.listOfPerm
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class HasPermissionsModule {

    @[Provides Singleton Named("hasPerm")]
    fun provideHasPermissions(context: Application):() -> Boolean = {
        listOfPerm.all {
            ActivityCompat.checkSelfPermission(context, it) == PERMISSION_GRANTED
        }
    }
}