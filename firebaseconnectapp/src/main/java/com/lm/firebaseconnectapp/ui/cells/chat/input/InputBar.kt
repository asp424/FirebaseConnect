package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.AnimDp
import com.lm.firebaseconnectapp.ui.UiStates.getReplyMessage
import com.lm.firebaseconnectapp.ui.UiStates.inputText
import com.lm.firebaseconnectapp.ui.cells.chat.animations.WritingAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.cells.ReplyBox

@Composable
fun InputBar(isWriting: String, state: LazyListState) {

    var xSend by remember { mutableStateOf(0.dp) }

    var xSendFile by remember { mutableStateOf(0.dp) }

    AnimDp(inputText.value.isEmpty(), 47.dp, 0.dp, delay = 100) { xSend = it }

    AnimDp(inputText.value.isNotEmpty(), 47.dp, 0.dp) { xSendFile = it }

    Column(Modifier.fillMaxSize(), Arrangement.Bottom, Alignment.Start) {
        isWriting.WritingAnimation()
        getReplyMessage.ReplyBox(state)
        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.Bottom) {
            InputTextField()
            Box() {
                SendFile(-xSendFile)
                SendButton(xSend)
            }
            RecordButton()
        }
    }
}