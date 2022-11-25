package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor

@Composable
fun MessageModel.ReplyBox() {
    if (isReply) {
        Canvas(Modifier) {
            drawLine(
                start = Offset(55f, 45f), end = Offset(55f, 130f),
                color = getMainColor,
                strokeWidth = 5f
            )
        }
        Column(modifier = Modifier.padding(start = 25.dp, top = 10.dp, end = 10.dp).wrapContentWidth()) {
            Text(replyName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = getMainColor)
            Text(replyText, maxLines = 1, fontSize = 12.sp)
        }
    }
}