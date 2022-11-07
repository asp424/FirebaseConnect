package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
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
                .padding(1.dp), border = BorderStroke(1.dp, getSecondColor)
        ) {
            Row(
                Modifier
                    .fillMaxSize().padding(start = 8.dp), SpaceBetween, CenterVertically
            ) {
                Box(Modifier.fillMaxHeight(), contentAlignment = CenterStart) {
                    Row(Modifier.wrapContentWidth(), Arrangement.Start, CenterVertically) {
                        SetImage(iconUri, onClick1 = onIconClick)
                        Column(modifier = Modifier.padding(start = 12.dp).offset(0.dp, (-2).dp)) {
                            Text(text = name.ifEmpty { id }, fontSize = 15.sp)
                            Text(
                                text = if (isWriting) "печатает..."
                                else {
                                    if (lastMessage.length >= 40)
                                        "${lastMessage.substring(0, 40)}..."
                                    else lastMessage
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
