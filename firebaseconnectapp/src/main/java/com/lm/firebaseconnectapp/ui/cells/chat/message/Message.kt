package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.cells.chat.message.text.TextMessage
import com.lm.firebaseconnectapp.ui.cells.chat.message.text.Time
import com.lm.firebaseconnectapp.ui.cells.chat.message.voice.VoiceMessage

@Composable
fun MessageModel.Message() = Box(Modifier.fillMaxWidth(), alignment)
{
    var timeSize by remember { mutableStateOf(IntSize.Zero) }

    Card(
        Modifier
            .padding(5.dp)
            .widthIn(0.dp, 250.dp)
            .onGloballyPositioned { timeSize = it.size }, shape =
        if (alignment == CenterEnd) RoundedCornerShape(20.dp, 20.dp, 0.dp, 20.dp)
        else RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp)
    ) {
        Box() {
            if (type == 2) VoiceMessage() else TextMessage()
            Time(timeSize)
        }
    }
}

