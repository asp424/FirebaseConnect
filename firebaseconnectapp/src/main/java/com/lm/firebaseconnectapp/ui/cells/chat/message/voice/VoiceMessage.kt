package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceLength
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MessageModel.VoiceMessage() {

    val onClick = with(mainDep) { remember { { voiceTimeStamp.play(recorder) } } }
    val isCurrentTimestamp = voiceTimeStamp == getCurrentPlayTimestamp
    val context = LocalContext.current as MainActivity
    var timeString by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf(Icons.Default.PlayArrow) }

    LaunchedEffect(true) { setButtonPlayOffset(false) }
    voiceTimeStamp.GetIcon { icon = it }
    isCurrentTimestamp.GetText { timeString = it }

    val rotate by animateFloatAsState(
        if (getButtonPlayOffset && isCurrentTimestamp) 1000f else 0f, tween(700),
        finishedListener = { setButtonPlayOffset(false) }
    )
    Row(Modifier, Center, CenterVertically) {

        Box {
            if (playerSessionId.value != 0
                && isCurrentTimestamp
                && getPlayerState == PlayerStates.PLAYING
            ) {
                AndroidView({ getVisualizer(context) }, Modifier.size(50.dp))
            }

            FloatingActionButton(
                onClick,
                Modifier
                    .padding(10.dp)
                    .size(30.dp)
                    .rotate(rotate),
                CircleShape, getMainColor
            ) { Icon(icon, null, tint = getSecondColor) }
        }

        Column(Modifier.padding(top = 10.dp, start = 3.dp)) {
            LinearProgressIndicator(
                progress =
                if (isCurrentTimestamp && getVoiceDuration != ZERO)
                    1f - ((getVoiceDuration.inWholeMilliseconds * 1f) /
                            getVoiceLength.inWholeMilliseconds)
                else 0f, color = getMainColor,
                modifier = Modifier
                    .width(100.dp)
                    .padding(end = 20.dp)
            )
            Text(
                timeString, fontSize = 10.sp, color = getMainColor,
                modifier = Modifier.padding(top = 9.dp)
            )
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

fun playingTimer(player: MediaPlayer) {
    CoroutineScope(IO).launch {
        while (true) {
            if (getVoiceDuration > ZERO && getPlayerState == PlayerStates.PLAYING) {
                player.apply {
                    if (isActive)
                        setVoiceDuration(duration.milliseconds - currentPosition.milliseconds)
                }
            } else break
        }
    }
}



