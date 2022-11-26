package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor

@Composable
fun MessageModel.ReplyBox() {
    if (isReply) {
        LocalDensity.current.apply {
            Canvas(Modifier) {
                drawLine(
                    start = Offset(16.dp.toPx(), 14.dp.toPx()), end = Offset(16.dp.toPx(), 42.dp.toPx()),
                    color = getMainColor,
                    strokeWidth = 4f
                )
            }
        }
        Column(modifier = Modifier.padding(start = 24.dp, top = 10.dp, end = 10.dp).wrapContentWidth()) {
            Text(replyName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = getMainColor)
            Text(replyText, maxLines = 1, fontSize = 12.sp)
        }
    }
}