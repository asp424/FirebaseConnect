package com.lm.firebaseconnectapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.cells.SettingsCard
import com.lm.firebaseconnectapp.ui.cells.TopBar

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavHost() {
    with(mainDep) {
        with(uiStates) {
            LaunchedEffect(getNavState){
                if (getNavState.isNotEmpty()) navController.navigate(getNavState)
            }
            Column() {
                TopBar {}
                AnimatedNavHost(
                    navController = navController,
                    startDestination = if (firebaseAuth.currentUser?.uid == null)
                        "reg" else "main"
                ) {

                    composable("reg", enterTransition = { enterLeftToRight },
                        exitTransition = { exitRightToLeft }) {
                       RegScreen()
                    }

                    composable("main", enterTransition = { enterLeftToRight },
                        exitTransition = { exitRightToLeft }) {
                        Main()
                        LaunchedEffect(true) {
                            true.setIsMainMode
                        }
                    }

                    composable("chat", enterTransition = { enterLeftToRight },
                        exitTransition = { exitLeftToRight }) {
                        Chat()
                        LaunchedEffect(true) {
                            false.setIsMainMode
                        }
                    }
                }
            }
            SettingsCard()
            IncomingCallScreen()
            OutgoingCallScreen()
        }
    }
}
