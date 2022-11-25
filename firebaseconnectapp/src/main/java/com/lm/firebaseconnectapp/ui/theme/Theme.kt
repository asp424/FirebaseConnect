package com.lm.firebaseconnectapp.ui.theme

import android.app.Activity
import android.media.Ringtone
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.one_tap_google_auth.OneTapGoogleAuth
import com.lm.firebaseconnectapp.di.compose.MainDep
import com.lm.firebaseconnectapp.notifications.NotificationReceiver
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.ui.UiInteractor
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun MainTheme(
    fBConnect: FirebaseConnect,
    ringtone: Ringtone,
    sPreferences: SPreferences,
    firebaseAuth: FirebaseAuth,
    oneTapGoogleAuth: OneTapGoogleAuth,
    uiInteractor: UiInteractor,
    notificationReceiver: NotificationReceiver,
    recorder: Recorder,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MainDep(
        fBConnect,
        ringtone,
        sPreferences,
        firebaseAuth,
        oneTapGoogleAuth,
        notificationReceiver,
        uiInteractor,
        recorder
    ) {

        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
        val view = LocalView.current
        val window = (view.context as Activity).window
        if (!view.isInEditMode) {
            window.statusBarColor =
                if (darkTheme) Black.toArgb() else getMainColor.toArgb()
            window.navigationBarColor =
                if (darkTheme) Black.toArgb() else getMainColor.toArgb()
            WindowCompat.getInsetsController((view.context as Activity).window, view)
                .isAppearanceLightStatusBars = darkTheme
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

