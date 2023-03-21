package com.lm.firebaseconnectapp.ui.cells.chat.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.getChatModel
import com.lm.firebaseconnectapp.ui.UiStates.getIsMainMode
import com.lm.firebaseconnectapp.ui.UiStates.getOnlineVisible
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.cells.SetImage

@Composable
fun InfoBox(scale: Float, onIconClick: () -> Unit = {}) {
    with(mainDep) {

        val userModel by remember {
            listMessages.value = UIMessagesStates.Loading
            derivedStateOf { sPreferences.getChatModel(firebaseConnect) }
        }

        with(firebaseConnect) {
            Row(
                Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .padding(top = 10.dp, end = 10.dp),
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
                        Modifier
                            .wrapContentWidth()
                            .scale(
                                animScale(
                                    target = getOnlineVisible
                                )
                            ),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {
                        SetImage(userModel.iconUri, onClick1 = onIconClick)
                        Column(
                            Modifier.padding(start = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = userModel.name.ifEmpty { userModel.id },
                                color = if (isSystemInDarkTheme()) Color.White else getSecondColor
                            )
                            Card(
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(top = 3.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (userModel.onLine) Color.Green else Color.Red
                                ),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text(
                                    text = if (userModel.onLine) "online" else "offline",
                                    modifier = Modifier.padding(
                                        start = 5.dp, end = 5.dp,
                                        top = 2.dp, bottom = 2.dp
                                    ),
                                    color = Color.White, fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Row(Modifier.padding(start = 30.dp)) {
                    Icon(
                        Icons.Default.Call, null, modifier = Modifier
                            .clickable(
                                !getIsMainMode,
                                onClick = remember { { remoteMessages.initialCall() } },
                            )
                            .padding(end = 10.dp)
                            .size(32.dp)
                            .scale(
                                animScale(!getIsMainMode && getOnlineVisible)
                            ),
                        tint = if (isSystemInDarkTheme()) Color.White else getSecondColor
                    )
                    Box(Modifier.padding(start = 10.dp)) {
                        Icon(Icons.Rounded.Delete, null,
                            tint = if (isSystemInDarkTheme()) Color.White else getSecondColor,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(animScale(!getIsMainMode && getOnlineVisible))
                                .clickable { deleteAllMessages() }
                        )
                    }
                }
            }
        }
    }
}
