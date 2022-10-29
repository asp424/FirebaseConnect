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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.ROOM
import com.lm.firebaseconnect.UserModel
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.firebaseConnect
import com.lm.firebaseconnectapp.startJitsiMit
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun OutgoingCallScreen(callUserModel: UserModel) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val height = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current
    var text by remember { mutableStateOf(false) }
    LaunchedEffect(callState.value) {
        text = if (callState.value.typeMessage == OUTGOING_CALL ||
            callState.value.typeMessage == GET_INCOMING_CALL
        ) {
            true
        } else {
            if (callState.value.typeMessage == ANSWER) {
                startJitsiMit(context, ROOM)
            }
            delay(2000)
            false
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .scale(
                animScale(
                    text
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(80.dp)
            ) {
                FloatingActionButton(
                    onClick = { firebaseConnect.reject(callUserModel.token) },
                    shape = CircleShape, containerColor = Color.Red
                ) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_phone_disabled_24), null,
                        Modifier
                            .size(50.dp)
                            .padding(10.dp), Color.White
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = callUserModel.id, color = Color.White)
                Text(
                    text = "Пошёл вызов у него", color = Color.White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == GET_INCOMING_CALL)
                    )
                )
                Text(
                    text = "Он взял трубку", color = Color.White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == ANSWER)
                    )
                )

                Text(
                    text = "Он отклонил вызов", color = Color.White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == RESET)
                    )
                )
                Text(
                    text = "Он отклонил вызов", color = Color.White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == REJECT)
                    )
                )
            }
        }
    }
}
