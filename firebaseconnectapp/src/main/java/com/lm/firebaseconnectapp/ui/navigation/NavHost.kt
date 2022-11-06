package com.lm.firebaseconnectapp.ui.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.UiStates.getSettingsVisible
import com.lm.firebaseconnectapp.ui.UiStates.setIsMainMode
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible
import com.lm.firebaseconnectapp.ui.UiStates.setUserModelChat
import com.lm.firebaseconnectapp.ui.cells.SettingsCard
import com.lm.firebaseconnectapp.ui.cells.TopBar
import com.lm.firebaseconnectapp.ui.screens.*

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavHost(startScreen: NavRoutes) {
    with(mainDep) {
            var onlineVisible by remember { mutableStateOf(false) }
            Column() {
                TopBar(onlineVisible) {}
                AnimatedNavHost(
                    navController = navController,
                    startDestination = startScreen.route
                ) {

                    composable(NavRoutes.REG.route, enterTransition = { enterLeftToRight },
                        exitTransition = { exitRightToLeft }) {
                        RegScreen()
                    }

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
                            true.setIsMainMode
                            setNavState(NavRoutes.EMPTY)
                        }
                    }

                    composable(NavRoutes.CHAT.route, enterTransition = { enterRightToLeft },
                        exitTransition = { exitLeftToRight }) {
                        LaunchedEffect(true) {
                            false.setIsMainMode
                            firebaseConnect
                                .setChatId(sPreferences.readChatUserModel().id.toInt())
                            setUserModelChat(sPreferences.readChatUserModel())
                            firebaseConnect.firebaseRead.startListener()
                        }
                        ChatScreen { onlineVisible = it }
                    }
                }
            }
            SettingsCard()
            IncomingCallScreen()
            OutgoingCallScreen()
            val activity = LocalContext.current as MainActivity
            BackHandler() {
                when (navController.currentDestination?.route) {
                    NavRoutes.CHAT.route -> {
                        if (GET_INCOMING_CALL.isType)
                            firebaseConnect.remoteMessages.cancelCall(get.token, REJECT)
                        else {
                            if (!OUTGOING_CALL.isType) navController.navigate(NavRoutes.MAIN.route)
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
