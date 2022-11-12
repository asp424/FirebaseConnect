package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset

@Composable
fun String.VoiceMessageInbox() {

    val onClick = with(mainDep) { remember { { play(recorder)} } }
    Row(Modifier, Center, CenterVertically) {
        FloatingActionButton(
            onClick,
            Modifier.padding(10.dp).size(40.dp).rotate(
                animateFloatAsState(
                    if (getButtonPlayOffset && substringAfter(IS_RECORD)
                        == getCurrentPlayTimestamp) 110f else 0f, tween(700)
                ).value
            )
        ) {
            Icon(
                if (substringAfter(IS_RECORD) == getCurrentPlayTimestamp) {
                    if (getPlayerState == PlayerStates.PAUSE || getPlayerState == PlayerStates.NULL
                    ) Icons.Default.PlayArrow else Icons.Default.Pause
                } else Icons.Default.PlayArrow, null
            )
        }
        Column {
            Text(substringAfter(IS_RECORD), Modifier.padding(end = 10.dp), fontSize = 12.sp)
            Text(
                text = if (substringAfter(IS_RECORD) == getCurrentPlayTimestamp) {
                    if (getVoiceDuration.toString() == "0s") "" else getVoiceDuration.toString()
                } else "", fontSize = 12.sp
            )
        }
    }
}

private fun String.play(recorder: Recorder) = with(recorder) {

    val isCurrentVoice = substringAfter(IS_RECORD) == getCurrentPlayTimestamp

    when (getPlayerState) {
        PlayerStates.PLAYING -> if (isCurrentVoice) pause() else stopAndPlay().apply { setButtonPlayOffset(true) }
        PlayerStates.NULL -> stopAndPlay().apply { setButtonPlayOffset(true) }
        PlayerStates.PAUSE -> if (isCurrentVoice) play() else stopAndPlay().apply { setButtonPlayOffset(true) }
    }
}

