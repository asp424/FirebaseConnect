package com.lm.firebaseconnectapp.ui.cells.chat.message.text

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.MessageModel

@Composable
fun MessageModel.TextMessage() {
    Text(
        text, Modifier.padding(
            start = 15.dp, bottom = 10.dp, top = 10.dp, end = 45.dp
        ), fontSize = 14.sp
    )
}
