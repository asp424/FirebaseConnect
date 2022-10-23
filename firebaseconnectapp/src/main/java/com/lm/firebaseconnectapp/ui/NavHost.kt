package com.lm.firebaseconnectapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController, startDestination = "main"
    ) {
        composable("main", enterTransition = { enterLeftToRight },
            exitTransition = { exitRightToLeft }) {
            Main(navController)
        }

        composable("chat", content = {
            Chat(navController)
        })
    }
}

