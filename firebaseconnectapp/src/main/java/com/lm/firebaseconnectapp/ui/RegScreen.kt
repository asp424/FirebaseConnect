package com.lm.firebaseconnectapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHost
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.toast

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun RegScreen() {
    with(mainDep) {
        with(uiStates) {
            val activity = LocalContext.current as MainActivity
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        oneTapGoogleAuth.startOTGAuth(
                            activity.regLauncher, activity.appComponent, activity.signInClient
                        ) { toast }
                    }, colors =
                    ButtonDefaults.buttonColors(containerColor = getMainColor)
                ) { Text(text = "Sign in with google", color = getSecondColor) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostReg() {
    with(mainDep) {
        AnimatedNavHost(
            navController = navController, startDestination = "reg"
        ) {

            composable("reg", enterTransition = { enterLeftToRight },
                exitTransition = { exitRightToLeft }) {
                RegScreen()
            }

            composable("main", enterTransition = { enterLeftToRight },
                exitTransition = { exitLeftToRight }) {
                Main()
            }
        }
    }
}