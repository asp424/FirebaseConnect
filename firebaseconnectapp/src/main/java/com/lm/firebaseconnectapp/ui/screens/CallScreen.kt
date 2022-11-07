package com.lm.firebaseconnectapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.callScreenVisible
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.outComingCallVisible
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.startJitsiMit
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.getUserModelChat
import com.lm.firebaseconnectapp.ui.cells.SetImage

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CallScreen() {
    with(mainDep) {
        val context = LocalContext.current
        Box(
            Modifier
                .fillMaxSize()
                .scale(animScale(callScreenVisible)), Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(getMainColor, getSecondColor)
                        ), alpha = 0.9f
                    )
                    .padding(80.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        firebaseConnect.remoteMessages.cancelCall(States.getToken, States.RESET)
                        ringtone.stop()
                    },
                    shape = CircleShape, containerColor = Color.Red,
                    modifier = Modifier.scale(animScale(INCOMING_CALL.isType))
                ) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_phone_disabled_24),
                        null,
                        Modifier
                            .size(50.dp)
                            .padding(10.dp), White
                    )
                }
                FloatingActionButton(
                    onClick = {
                        if (GET_INCOMING_CALL.isType)
                            firebaseConnect.remoteMessages.cancelCall(get.token, REJECT)
                        if (States.OUTGOING_CALL.isType)
                            firebaseConnect.remoteMessages.cancel(getUserModelChat.id)
                    },
                    shape = CircleShape, containerColor = Color.Red, modifier =
                    Modifier.scale(animScale(outComingCallVisible))
                ) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_phone_disabled_24), null,
                        Modifier
                            .size(50.dp)
                            .padding(10.dp), White
                    )
                }
                FloatingActionButton(
                    onClick = {
                        ringtone.stop()
                        firebaseConnect.remoteMessages.answer(States.getToken)
                        startJitsiMit(
                            context, States.getToken, firebaseConnect.myName, firebaseConnect.myIcon
                        )
                    }, shape = CircleShape, containerColor = Green,
                    modifier = Modifier.scale(animScale(INCOMING_CALL.isType))
                ) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_phone_24), null,
                        Modifier
                            .size(50.dp)
                            .padding(10.dp), White
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                SetImage(get.icon, false)
                Text(get.name, color = getSecondColor, modifier = Modifier.padding(top = 10.dp))
                Text(get.title, color = getSecondColor)
            }
        }
    }
}