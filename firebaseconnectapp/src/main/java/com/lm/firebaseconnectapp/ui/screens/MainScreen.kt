package com.lm.firebaseconnectapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.cells.UserCard
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen() {
    with(mainDep) {
        with(firebaseConnect) {
            SetContent {
                    if (listUsers.value is UIUsersStates.Success) {
                        Column(
                            Modifier
                                .fillMaxSize()
                        ) {
                            (listUsers.value as UIUsersStates.Success).list.forEach { model ->
                                UserCard(model,
                                    onCardClick = {
                                        sPreferences.saveChatUserModel(model)
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
