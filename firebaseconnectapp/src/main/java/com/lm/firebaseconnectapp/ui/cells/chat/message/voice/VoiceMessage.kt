package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.getVisualizer
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.ui.UiStates.getButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset

@Composable
fun MessageModel.VoiceMessage() {

    val onClick = with(mainDep) { remember { { timeStamp.play(recorder) } } }
    val isCurrentTimestamp = timeStamp == getCurrentPlayTimestamp
    val context = LocalContext.current as MainActivity
    var timeString by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf(Icons.Default.PlayArrow) }

    LaunchedEffect(true) { setButtonPlayOffset(false) }
    timeStamp.GetIcon { icon = it }
    isCurrentTimestamp.GetText { timeString = it }

    val rotate by animateFloatAsState(
        if (getButtonPlayOffset && isCurrentTimestamp) 1000f else 0f, tween(700),
        finishedListener = { setButtonPlayOffset(false) }
    )
    Card(Modifier.padding(5.dp)) {
    Row(Modifier, Center, CenterVertically) {

        Box {
            if (playerSessionId.value != 0 && isCurrentTimestamp) {
                AndroidView({ getVisualizer(context) }, Modifier.size(60.dp))
            }

            FloatingActionButton(
                onClick,
                Modifier
                    .padding(10.dp)
                    .size(40.dp)
                    .rotate(rotate),
                CircleShape, getMainColor
            ) { Icon(icon, null, tint = getSecondColor) }
        }

            Column(Modifier.padding(10.dp)) {
                Text("Звукозапись", Modifier.padding(end = 10.dp), getMainColor, 12.sp)
                Text(timeString, fontSize = 12.sp, color = getMainColor)
            }
        }
    }
}

    fun String.play(recorder: Recorder) = with(recorder) {

        val isCurrentVoice = this@play == getCurrentPlayTimestamp

        when (getPlayerState) {
            PlayerStates.PLAYING -> if (isCurrentVoice) pause() else stopAndPlay()
            PlayerStates.NULL -> stopAndPlay()
            PlayerStates.PAUSE -> if (isCurrentVoice) play() else stopAndPlay()
        }
    }



