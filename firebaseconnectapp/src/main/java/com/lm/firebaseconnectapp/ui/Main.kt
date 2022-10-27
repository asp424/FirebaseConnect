package com.lm.firebaseconnectapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.UIUsersStates
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnect.listUsers
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.firebaseConnect

@Composable
fun Main(navController: NavHostController) {
    var callToken by remember(){ mutableStateOf("") }
    firebaseConnect.SetMainScreenContent {
        if (listUsers.value is UIUsersStates.Success) {
            Column() {
                (listUsers.value as UIUsersStates.Success).list.forEach {
                    Note(
                        modifier = Modifier,
                        notesText = it.isWriting,
                        i = it.id,
                        navController = navController,
                        online = it.onLine,
                        token = it.token
                    ){ t ->
                        callToken = t
                    }
                }
            }
            SettingsCard()
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    Modifier
                        .scale(
                            animateFloatAsState(
                                if (callState.value.typeMessage == OUTGOING_CALL
                                    || callState.value.typeMessage == GET_INCOMING_CALL
                                ) 1f else 0f
                            ).value
                        )
                        .fillMaxSize()
                ) {
                    Row(
                        Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            Icons.Default.Close, null, modifier = Modifier.clickable {
                                firebaseConnect.reject(callToken)
                            }
                        )
                    }
                }
            }

        } else Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}