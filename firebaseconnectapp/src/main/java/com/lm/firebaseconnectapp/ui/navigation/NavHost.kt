package com.lm.firebaseconnectapp.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.UiStates.getSettingsVisible
import com.lm.firebaseconnectapp.ui.UiStates.setIsMainMode
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible
import com.lm.firebaseconnectapp.ui.cells.SettingsCard
import com.lm.firebaseconnectapp.ui.cells.TopBar
import com.lm.firebaseconnectapp.ui.cells.VoiceBar
import com.lm.firebaseconnectapp.ui.cells.getChatModel
import com.lm.firebaseconnectapp.ui.screens.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavHost(startScreen: NavRoutes) {
    val activity = LocalContext.current as MainActivity
    with(mainDep) {
        Scaffold(
            Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
        ) {
            Column() {
                TopBar()
                VoiceBar()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = startScreen.route
                ) {

                    composable(NavRoutes.REG.route, enterTransition = { enterLeftToRight },
                        exitTransition = { exitRightToLeft }) { RegScreen() }

                    composable(NavRoutes.MAIN.route, enterTransition = {
                        when (initialState.destination.route) {
                            NavRoutes.CHAT.route -> enterLeftToRight
                            else -> enterRightToLeft
                        }
                    },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavRoutes.CHAT.route -> exitRightToLeft
                                else -> exitLeftToRight
                            }
                        }) {
                        MainScreen()
                        LaunchedEffect(true) {
                            setIsMainMode(true)
                            setNavState(NavRoutes.EMPTY)
                        }
                    }

                    composable(NavRoutes.CHAT.route, enterTransition = { enterRightToLeft },
                        exitTransition = { exitLeftToRight }) {
                        LaunchedEffect(true) {
                            setIsMainMode(false)
                        }
                        ChatScreen()
                    }
                }
            }
            SettingsCard()
            CallScreen()
            BackHandler() {
                with(sPreferences.getChatModel(firebaseConnect)) {
                    when (navController.currentDestination?.route) {
                        NavRoutes.CHAT.route -> {
                            if (GET_INCOMING_CALL.isType) firebaseConnect.remoteMessages.cancelCall(
                                REJECT, token, id, firebaseConnect.myDigit
                            )
                            else {
                                if (!OUTGOING_CALL.isType) setNavState(NavRoutes.MAIN)
                                else firebaseConnect.remoteMessages.cancel(id)
                            }
                        }

                        NavRoutes.MAIN.route -> {
                            if (getSettingsVisible) false.setSettingsVisible
                            else activity.finish()
                        }

                        NavRoutes.REG.route -> activity.finish()
                    }
                }
            }
        }
    }
}

