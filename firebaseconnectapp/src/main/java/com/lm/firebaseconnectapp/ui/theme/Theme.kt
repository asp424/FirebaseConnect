package com.lm.firebaseconnectapp.ui.theme

import android.app.Activity
import android.media.Ringtone
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.core.NotificationReceiver
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import com.lm.firebaseconnectapp.di.compose.MainDep
import com.lm.firebaseconnectapp.ui.UiInteractor
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.setMainColor
import com.lm.firebaseconnectapp.ui.UiStates.setSecondColor

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
        uiInteractor
    ) {
        LaunchedEffect(true){
            Color(sPreferences.readMainColor()).setMainColor
            Color(sPreferences.readSecondColor()).setSecondColor
        }
        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
        val view = LocalView.current
        if (!view.isInEditMode) {
            getMainColor.also { color ->
                LaunchedEffect(color) {
                    (view.context as Activity).window.statusBarColor = color.toArgb()
                    (view.context as Activity).window.navigationBarColor = color.toArgb()
                    WindowCompat.getInsetsController((view.context as Activity).window, view)
                        .isAppearanceLightStatusBars = darkTheme
                }
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
