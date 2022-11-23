package com.lm.firebaseconnectapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.fantasticprogress.CircleProgress
import com.lm.fantasticprogress.ProgressCircleType
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceBarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.cells.main.UserCard
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen() {
    with(mainDep) {
        with(firebaseConnect) {
            val state = rememberLazyListState()
            SetContent {
                Scaffold(
                    Modifier
                        .fillMaxSize()
                        .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                ) {
                    if (listUsers.value is UIUsersStates.Success) {
                        val listMessages =
                            (listUsers.value as UIUsersStates.Success).list
                        LazyColumn(
                            state = state,
                            content = {
                                items(
                                    count = listMessages.size,
                                    key = {
                                        derivedStateOf { listMessages[it].id }.value
                                    },
                                    itemContent = {
                                        UserCard(listMessages[it],
                                            onCardClick = {
                                                sPreferences.saveChatId(listMessages[it].id)
                                                setNavState(NavRoutes.CHAT)
                                            }, onIconClick = {

                                            })
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = animDp(getVoiceBarVisible, 25.dp, 0.dp)),
                            contentPadding = PaddingValues(
                                start = 10.dp, end = 10.dp, top = 10.dp
                            )
                        )
                    } else Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) {
                        CircleProgress(
                            ProgressCircleType.Atom, 0, 5,
                            listUsers.value is UIUsersStates.Loading
                        )
                    }
                }
            }
        }
    }
}
