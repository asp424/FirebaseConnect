package com.lm.firebaseconnectapp.ui.cells.chat.message

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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.lm.firebaseconnectapp.databinding.VisualizerBinding
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.ui.UiStates.getButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset

@Composable
fun String.VoiceMessageInbox() {
    LaunchedEffect(true){
        setButtonPlayOffset(false)
    }
    val timestamp = substringAfter(IS_RECORD)
    val onClick = with(mainDep) { remember { { timestamp.play(recorder) } } }
    val isCurrentTimestamp = timestamp == getCurrentPlayTimestamp
    val context = LocalContext.current as MainActivity
    val getVisualizer = remember {
        {
            VisualizerBinding.inflate(context.layoutInflater).root.apply {
                setColor(getMainColor.toArgb())
                setPlayer(playerSessionId.value)
            }
        }
    }

    Row(Modifier, Center, CenterVertically) {
        Box {
            if (playerSessionId.value != 0 && isCurrentTimestamp) {
                AndroidView(
                    { getVisualizer() },
                    Modifier
                        .size(60.dp)
                )
            }
            FloatingActionButton(
                onClick,
                Modifier
                    .padding(10.dp)
                    .size(40.dp)
                    .rotate(
                        animateFloatAsState(
                            if (getButtonPlayOffset && isCurrentTimestamp) 1000f else 0f,
                            finishedListener = {
                                setButtonPlayOffset(false)
                            }, animationSpec = tween(700)
                        ).value
                    ), CircleShape, containerColor = getMainColor
            ) {
                Icon(
                    if (isCurrentTimestamp) {
                        if (getPlayerState == PlayerStates.PAUSE || getPlayerState == PlayerStates.NULL
                        ) Icons.Default.PlayArrow else Icons.Default.Pause
                    } else Icons.Default.PlayArrow, null, tint = getSecondColor
                )
            }
        }
        Column {
            Text(
                "Звукозапись", Modifier.padding(end = 10.dp),
                fontSize = 12.sp, color = getMainColor
            )
            Text(
                text = if (isCurrentTimestamp) {
                    if (getVoiceDuration.toString() == "0s") "" else getVoiceDuration.toString()
                } else "", fontSize = 12.sp, color = getMainColor
            )
        }
    }
}

fun String.play(recorder: Recorder) = with(recorder) {

    val isCurrentVoice = substringAfter(IS_RECORD) == getCurrentPlayTimestamp

    when (getPlayerState) {
        PlayerStates.PLAYING -> if (isCurrentVoice) pause() else stopAndPlay()
        PlayerStates.NULL -> stopAndPlay()
        PlayerStates.PAUSE -> if (isCurrentVoice) play() else stopAndPlay()
    }
}

