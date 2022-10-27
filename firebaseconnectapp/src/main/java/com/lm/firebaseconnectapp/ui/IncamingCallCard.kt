package com.lm.firebaseconnectapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.firebaseConnect

@Composable
fun SettingsCard() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .scale(
                    animScale(
                        callState.value.typeMessage == INCOMING_CALL
                    )
                )
                .fillMaxSize(), colors = CardDefaults.cardColors(
                containerColor = Color.White
            ), border = BorderStroke(3.dp, Color.Blue)
        ) {
            Column() {
                Box(Modifier, contentAlignment = Alignment.Center) {
                    Text(text = callState.value.name)
                }
                Row(
                    Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(painterResource(R.drawable.a), null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                firebaseConnect.reset(callState.value.token)
                            }
                            .background(Color.White)
                    )

                    Icon(
                        painterResource(id = R.drawable.a), null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
