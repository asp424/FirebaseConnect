package com.lm.firebaseconnectapp.ui.cells.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.FirebaseRead
import com.lm.firebaseconnect.FirebaseRead.Companion.R_T_S
import com.lm.firebaseconnect.TimeConverter.Companion.T_T_E
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.cells.SetImage
import com.lm.firebaseconnectapp.ui.theme.darkGreen

@Composable
fun UserCard(
    userModel: UserModel, onCardClick: () -> Unit, onIconClick: () -> Unit
) {
    with(userModel) {
        OutlinedCard(
            Modifier
                .clickable(onClick = onCardClick)
                .height(60.dp)
                .padding(1.dp), border = BorderStroke(
                1.dp, if (isSystemInDarkTheme()) White else getMainColor
            )
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp), SpaceBetween, CenterVertically
            ) {
                Box(Modifier.fillMaxHeight(), contentAlignment = CenterStart) {
                    Row(Modifier.wrapContentWidth(), Arrangement.Start, CenterVertically) {
                        SetImage(iconUri, onClick1 = onIconClick)
                        Column(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .offset(0.dp, (-2).dp)
                        ) {
                            Text(text = name.ifEmpty { id }, fontSize = 15.sp)
                            Text(
                                text = when (isWriting) {
                                    "1" -> "writing..."
                                    "2" -> "recording voice..."
                                    else -> { if (lastMessage.contains(IS_RECORD)) "voice"
                                        else {
                                            with(lastMessage.substringAfter(T_T_E)
                                                .substringBefore(R_T_S)) {
                                                if (length >= 30)
                                                    "${substring(0, 30)}..."
                                                else this
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.padding(top = 2.dp),
                                fontSize = 12.sp,
                                fontStyle = Italic
                            )
                        }
                    }
                    Box(Modifier.offset(38.dp, 12.dp)) {
                        Canvas(Modifier.scale(animScale(onLine))) {
                            drawCircle(darkGreen, 5.dp.toPx())
                        }
                    }
                }
            }
        }
    }
}
