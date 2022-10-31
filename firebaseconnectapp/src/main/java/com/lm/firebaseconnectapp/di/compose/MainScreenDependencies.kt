package com.lm.firebaseconnectapp.di.compose

import android.media.Ringtone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.di.dagger.AppComponent

data class MainDependencies(
    val firebaseConnect: FirebaseConnect,
    val ringtone: Ringtone
)

@Composable
fun MainScreenDependencies(
    appComponent: AppComponent,
    content: @Composable () -> Unit
) = with(appComponent){
    CompositionLocalProvider(
        Local provides MainDependencies(firebaseConnect(), ringtone()),
        content = content
    )
}

private val Local = staticCompositionLocalOf<MainDependencies> { error("No value provided") }

object MainDep {
    val mainDep @Composable get() = Local.current
}

