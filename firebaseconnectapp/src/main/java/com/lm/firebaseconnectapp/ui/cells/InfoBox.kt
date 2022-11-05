package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep

@Composable
fun InfoBox(scale: Float, userModel: UserModel, onLineVisible: Boolean, onIconClick: () -> Unit) {
    with(userModel) {
        with(mainDep) {
            with(firebaseConnect) {
                with(uiStates) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .scale(scale)
                            .padding(start = 10.dp)
                            .padding(10.dp),
                        Arrangement.Absolute.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .scale(animScale(!getIsMainMode)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                Modifier.wrapContentWidth(),
                                Arrangement.Start,
                                Alignment.CenterVertically
                            ) {
                                if (name.isEmpty())
                                    DrawCircle(name.ifEmpty { id }, onClick = onIconClick)
                                else SetImage(
                                    iconUri,
                                    onIconClick
                                )
                                Column(Modifier.padding(start = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = name.ifEmpty { id },
                                        color = getSecondColor
                                    )
                                    Card(
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .scale(
                                                animScale(
                                                    target = onLineVisible
                                                )
                                            ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (onLineState.value) Color.Green else Color.Red
                                        ),
                                        border = BorderStroke(1.dp, Color.White)
                                    ) {
                                        Text(
                                            text = if (onLineState.value) "online" else "offline",
                                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                                            color = Color.White, fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                        Row(Modifier.padding(start = 20.dp, top = 5.dp)) {
                            Icon(
                                Icons.Default.Call, null, modifier = Modifier
                                    .clickable(
                                        onClick = remember {
                                            {
                                                remoteMessages.initialCall(getUserModelChat)
                                            }
                                        })
                                    .padding(end = 10.dp)
                                    .size(32.dp)
                                    .scale(animScale(!getIsMainMode)),
                                tint = getSecondColor
                            )
                            Box(Modifier.padding(start = 10.dp)) {
                                Icon(Icons.Rounded.Delete, null,
                                    tint = getSecondColor,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .scale(animScale(!getIsMainMode))
                                        .clickable { deleteAllMessages() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}