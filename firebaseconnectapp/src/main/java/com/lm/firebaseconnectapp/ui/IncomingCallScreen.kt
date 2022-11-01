package com.lm.firebaseconnectapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.startJitsiMit
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun IncomingCallScreen() {
    with(mainDep) {
        var screen by remember { mutableStateOf(false) }
        LaunchedEffect(callState.value) {
            screen = if (callState.value.typeMessage == INCOMING_CALL) {
                true
            } else {
                delay(2000)
                false
            }
        }
        val context = LocalContext.current
        Column(
            Modifier
                .fillMaxSize()
                .scale(
                    animScale(screen)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black)
                        .padding(80.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            firebaseConnect.remoteMessages.reset(callState.value.token); ringtone.stop()
                        },
                        shape = CircleShape, containerColor = Red
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_baseline_phone_disabled_24), null,
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), White
                        )
                    }
                    FloatingActionButton(onClick = {
                        ringtone.stop()
                        firebaseConnect.remoteMessages.answer(callState.value.token)
                        startJitsiMit(context, callState.value.token)
                    }, shape = CircleShape, containerColor = Green) {
                        Icon(
                            painterResource(R.drawable.ic_baseline_phone_24), null,
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), White
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = callState.value.callingId, color = White)
                    Text(
                        text = callState.value.name, color = White
                    )
                }
            }
        }
    }
}