package com.lm.firebaseconnectapp.ui.cells

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
import com.lm.firebaseconnectapp.ui.theme.darkGreen

@Composable
fun UserCard(
    userModel: UserModel, onCardClick: () -> Unit, onIconClick: () -> Unit
) {
    with(userModel) {
        OutlinedCard(
            Modifier
                .clickable(onClick = onCardClick)
                .height(90.dp)
                .padding(5.dp)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp), SpaceBetween, CenterVertically
            ) {
                Box(Modifier.fillMaxHeight(), contentAlignment = CenterStart) {
                    Row(Modifier.wrapContentWidth(), Arrangement.Start, CenterVertically) {
                        SetImage(iconUri, onIconClick)
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(text = name.ifEmpty { id })
                            Text(
                                text = if (isWriting) "печатает..."
                                else {
                                    if (lastMessage.length >= 30)
                                        "${lastMessage.substring(0, 30)}..."
                                    else lastMessage
                                },
                                modifier = Modifier.padding(top = 2.dp),
                                fontSize = 12.sp,
                                fontStyle = Italic
                            )
                        }
                    }
                    Box(Modifier.offset(43.dp, 16.dp)) {
                        Canvas(Modifier.scale(animScale(onLine))) {
                            drawCircle(darkGreen, 5.dp.toPx())
                        }
                    }
                }
            }
        }
    }
}
