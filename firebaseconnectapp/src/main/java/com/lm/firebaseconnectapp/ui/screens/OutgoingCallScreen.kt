package com.lm.firebaseconnectapp.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun OutgoingCallScreen() {
    with(mainDep.firebaseConnect) {
        var screen by remember { mutableStateOf(false) }
        LaunchedEffect(get) {
            screen =
                if (OUTGOING_CALL.isType || GET_CHECK_FOR_CALL.isType || GET_INCOMING_CALL.isType
                ) {
                    true
                } else {
                    delay(2000); false
                }
        }
        Column(
            Modifier
                .fillMaxSize()
                .scale(animScale(screen)),
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
                        onClick = { remoteMessages.cancelCall(get.token, REJECT) },
                        shape = CircleShape, containerColor = Color.Red, modifier =
                        Modifier.scale(animScale(GET_INCOMING_CALL.isType))
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
                    Text(text = get.callingId, color = Color.White)
                    Text(text = get.title, color = Color.White)
                }
            }
        }

    }
}