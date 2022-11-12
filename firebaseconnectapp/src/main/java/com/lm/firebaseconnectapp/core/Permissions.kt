package com.lm.firebaseconnectapp.core

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.lm.firebaseconnectapp.presentation.MainActivity
import javax.inject.Inject
import javax.inject.Named

interface Permissions {

    fun launchIfHasPermissions(activity: MainActivity, unit: () -> Unit)

    class Base @Inject constructor(
        @Named("hasPerm") private val hasPermissions: () -> Boolean
    ) : Permissions {

        private fun permissionsLauncherRegistration(
            activity: MainActivity,
            onAllPermissionsGet: () -> Unit
        ) = activity.registerForActivityResult(

            ActivityResultContracts.RequestMultiplePermissions()

        ) { result ->
            if (result.entries.all { it.value }) onAllPermissionsGet()
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun launchIfHasPermissions(activity: MainActivity, unit: () -> Unit) {

            if (hasPermissions()) unit() else
                permissionsLauncherRegistration(activity) { unit() }.launch(listOfPerm)
        }

        companion object {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            val listOfPerm = arrayOf(
                POST_NOTIFICATIONS, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
            )
        }
    }
}




