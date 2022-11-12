package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD

@Composable
fun String.TextMessageInbox() = Column(Modifier, Center, Start) {
    with(mainDep.firebaseConnect) {
        with(removeMessageKey().trimStart()) {
            if (startsWith(IS_RECORD)) VoiceMessageInbox() else {
                Text(this, Modifier.padding(10.dp, 5.dp, 10.dp, 5.dp), fontSize = 14.sp)
            }
        }
    }
}