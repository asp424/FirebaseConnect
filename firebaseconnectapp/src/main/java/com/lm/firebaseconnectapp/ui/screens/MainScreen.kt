package com.lm.firebaseconnectapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.cells.UserCard
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen() {
    with(mainDep) {
        with(firebaseConnect) {
            SetContent {
                Scaffold(
                    Modifier
                        .fillMaxSize()
                        .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                ) {
                    if (listUsers.value is UIUsersStates.Success) {
                        Column(

                        ) {
                            (listUsers.value as UIUsersStates.Success).list.forEach { model ->
                                UserCard(model,
                                    onCardClick = {
                                        sPreferences.saveChatId(model.id)
                                        setNavState(NavRoutes.CHAT)
                                    }, onIconClick = {

                                    })
                            }
                        }
                    } else Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
        }
    }
}
