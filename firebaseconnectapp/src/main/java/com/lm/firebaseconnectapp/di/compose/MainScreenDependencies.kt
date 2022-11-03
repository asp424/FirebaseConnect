package com.lm.firebaseconnectapp.di.compose

import android.media.Ringtone
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.UiStates
import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import com.lm.firebaseconnectapp.di.dagger.AppComponent

data class MainDependencies(
    val firebaseConnect: FirebaseConnect,
    val ringtone: Ringtone,
    val uiStates: UiStates,
    val sPreferences: SPreferences,
    val firebaseAuth: FirebaseAuth,
    val oneTapGoogleAuth: OneTapGoogleAuth,
    val navController: NavHostController
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainDep(
    appComponent: AppComponent,
    content: @Composable () -> Unit
) = with(appComponent) {
    CompositionLocalProvider(
        Local provides MainDependencies(
            fBConnect(),
            ringtone(),
            uiStates(),
            sPreferences(),
            firebaseAuth(),
            oneTapGoogleAuth(),
            rememberAnimatedNavController()
        ),
        content = content
    )
}

private val Local = staticCompositionLocalOf<MainDependencies> { error("No value provided") }

object MainDep {
    val mainDep @Composable get() = Local.current
}

