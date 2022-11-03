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
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.getToken
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.startJitsiMit
import com.lm.firebaseconnectapp.R
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun IncomingCallScreen() {
    with(mainDep) {
        var screen by remember { mutableStateOf(false) }
        LaunchedEffect(get) {
            screen = if (INCOMING_CALL.isType) {
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
                .scale(animScale(screen)),
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
                            firebaseConnect.remoteMessages.cancelCall(getToken, RESET)
                            ringtone.stop()
                        },
                        shape = CircleShape, containerColor = Red
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_baseline_phone_disabled_24),
                            null,
                            Modifier
                                .size(50.dp)
                                .padding(10.dp), White
                        )
                    }
                    FloatingActionButton(onClick = {
                        ringtone.stop()
                        firebaseConnect.remoteMessages.answer(getToken)
                        startJitsiMit(context, getToken)
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
                    Text(text = get.name, color = White)
                    Text(
                        text = get.title, color = White
                    )
                }
            }
        }
    }
}