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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.StateController
import com.lm.firebaseconnect.States.callScreenVisible
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.getCallingState
import com.lm.firebaseconnect.States.getToken
import com.lm.firebaseconnect.States.isOutgoingCall
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.SoundController
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.getChatModel
import com.lm.firebaseconnectapp.startJitsiMit
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.cells.SetImage

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CallScreen() {
    StateController()
    SoundController()
    with(mainDep) {
        val context = LocalContext.current
        val userModel by remember {
            listMessages.value = UIMessagesStates.Loading
            derivedStateOf {
                sPreferences.getChatModel(firebaseConnect)
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .scale(animScale(callScreenVisible.value)), Alignment.Center
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
                        firebaseConnect.remoteMessages.cancelCall(
                            RESET, getToken, get.destinationId, get.callingId
                        )
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
                            firebaseConnect.remoteMessages.cancelCall(
                                REJECT, userModel.token, userModel.id, firebaseConnect.myDigit
                            )
                        if (OUTGOING_CALL.isType)
                            firebaseConnect.remoteMessages.cancel(userModel.id)
                    },
                    shape = CircleShape, containerColor = Color.Red, modifier =
                    Modifier.scale(animScale(isOutgoingCall))
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
                        firebaseConnect.remoteMessages
                            .callMessage(
                                ANSWER,
                                firebaseConnect.remoteMessages.getMyDigit,
                                getToken
                            )
                        startJitsiMit(
                            context,
                            getToken,
                            firebaseConnect.myName,
                            firebaseConnect.myIcon
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
                SetImage(
                    if (isOutgoingCall || BUSY.isType) userModel.iconUri
                    else get.icon, false
                )
                Text(
                    if (isOutgoingCall || BUSY.isType) userModel.name
                    else get.name,
                    color = getSecondColor, modifier = Modifier.padding(top = 10.dp)
                )
                Text(getCallingState(), color = getSecondColor)
            }
        }
    }
}
