package com.lm.firebaseconnectapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lm.firebaseconnect.models.UserModel

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavHost() {
    var callUserModel by remember() { mutableStateOf(UserModel()) }
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController, startDestination = "main"
    ) {
        composable("main", enterTransition = { enterLeftToRight },
            exitTransition = { exitRightToLeft }) {
            Main(navController) { callUserModel = it }
        }

        composable("chat", enterTransition = { enterLeftToRight },
            exitTransition = { exitLeftToRight }){
            Chat(navController)
        }
    }
    IncomingCallScreen(callUserModel)
    OutgoingCallScreen(callUserModel)
}
