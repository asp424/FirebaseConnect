package com.lm.firebaseconnectapp.di.compose

import android.media.MediaPlayer
import android.media.Ringtone
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.notifications.NotificationReceiver
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.one_tap_google_auth.OneTapGoogleAuth
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.ui.UiInteractor

data class MainDependencies(
    val firebaseConnect: FirebaseConnect,
    val ringtone: Ringtone,
    val sPreferences: SPreferences,
    val firebaseAuth: FirebaseAuth,
    val oneTapGoogleAuth: OneTapGoogleAuth,
    val navController: NavHostController,
    val uiInteractor: UiInteractor,
    val notificationReceiver: NotificationReceiver,
    val waitSound: MediaPlayer,
    val callSound: MediaPlayer,
    val rejectSound: MediaPlayer,
    val recorder: Recorder
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainDep(
    fBConnect: FirebaseConnect,
    ringtone: Ringtone,
    sPreferences: SPreferences,
    firebaseAuth: FirebaseAuth,
    oneTapGoogleAuth: OneTapGoogleAuth,
    notificationReceiver: NotificationReceiver,
    uiInteractor: UiInteractor,
    recorder: Recorder,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    CompositionLocalProvider(
        Local provides MainDependencies(
            remember { fBConnect },
            remember { ringtone },
            remember { sPreferences },
            remember { firebaseAuth },
            remember { oneTapGoogleAuth },
            rememberAnimatedNavController(),
            remember { uiInteractor }, remember { notificationReceiver },
            remember {
                MediaPlayer.create(context, R.raw.zvukomir_2309_zvuk_ozhidanija_otveta).apply {
                    isLooping = true
                }
            },
            remember {
                MediaPlayer.create(context, R.raw.a).apply {
                    isLooping = true
                }
            },
            remember {
                MediaPlayer.create(context, R.raw.zvukomir_2309_zvuk_ozhidanija_otveta).apply {
                    isLooping = true
                }
            }, recorder
        ), content = content
    )
}

private val Local = staticCompositionLocalOf<MainDependencies> { error("No value provided") }

object MainDep {
    val mainDep @Composable get() = Local.current
}

