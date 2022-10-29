package com.lm.firebaseconnectapp.ui

import android.media.RingtoneManager
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.RemoteMessageModel
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.ROOM
import com.lm.firebaseconnect.UserModel
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnect.remoteMessageModel
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.firebaseConnect
import com.lm.firebaseconnectapp.startJitsiMit
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun IncomingCallScreen(callUserModel: UserModel) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val height = LocalConfiguration.current.screenHeightDp.dp
    var text by remember { mutableStateOf(false) }
    var typeMessage by remember { mutableStateOf("") }
    LaunchedEffect(callState.value) {
        text = if (callState.value.typeMessage == INCOMING_CALL) {
            true
        } else {
            delay(2000)
            false
        }
    }
    val context = LocalContext.current
    val standardNotificationUri = remember {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    }
    val sound = remember { RingtoneManager.getRingtone(context, standardNotificationUri) }
    Column(
        Modifier
            .fillMaxSize()
            .scale(
                animScale(text)
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
                    onClick = { firebaseConnect.reset(callUserModel.token) },
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
                    sound.stop()
                    firebaseConnect.answer(callUserModel.token)
                    callState.value = remoteMessageModel.rejectCall
                    startJitsiMit(context, ROOM)
                }, shape = CircleShape, containerColor = Green) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_phone_24), null,
                        Modifier
                            .size(50.dp)
                            .padding(10.dp), White
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = callState.value.callingId, color = White)
                Text(
                    text = "Он отменил вызов", color = White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == RESET
                                || callState.value.typeMessage == REJECT)
                    )
                )
                Text(
                    text = "Он взял трубку", color = White, modifier =
                    Modifier.scale(
                        animScale(callState.value.typeMessage == ANSWER)
                    )
                )
            }
        }
    }

    LaunchedEffect(callState.value.typeMessage) {
        if (callState.value.typeMessage == INCOMING_CALL)
            sound.apply { isLooping = true }.play()
    }
    DisposableEffect(callState.value.typeMessage) { onDispose { sound.stop() } }
}
