package com.lm.firebaseconnectapp.ui.cells.chat.message.text

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.R

@Composable
fun MessageModel.TextMessage() =
    Text(text, Modifier.padding(15.dp, if (isReply) 0.dp else 10.dp, 45.dp, 10.dp))
