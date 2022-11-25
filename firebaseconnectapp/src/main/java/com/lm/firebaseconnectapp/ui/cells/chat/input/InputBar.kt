package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.ui.UiStates.getReplyMessage
import com.lm.firebaseconnectapp.ui.cells.chat.animations.WritingAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.cells.ReplyBox

@Composable
fun MutableState<String>.InputBar(isWriting: String, state: LazyListState) {

    Column(
        Modifier
            .fillMaxSize(), Arrangement.Bottom, Alignment.Start
    ) {
        isWriting.WritingAnimation()
        getReplyMessage.ReplyBox(state)
        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.Bottom) {
            InputTextField()
            SendButton()
            RecordButton()
        }
    }
}