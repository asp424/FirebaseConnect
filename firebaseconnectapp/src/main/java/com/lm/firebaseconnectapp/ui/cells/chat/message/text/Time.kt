package com.lm.firebaseconnectapp.ui.cells.chat.message.text

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.MessageModel

@Composable
fun MessageModel.Time(timeSize: DpSize) = Text(
        time, Modifier.offset(
            timeSize.width - 40.dp, if (!isReply) timeSize.height - 20.dp else
                timeSize.height - 65.dp
        ), Gray, 10.sp
    )

