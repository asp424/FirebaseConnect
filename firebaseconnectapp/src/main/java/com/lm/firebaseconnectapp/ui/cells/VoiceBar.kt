package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.playingSendTime
import com.lm.firebaseconnectapp.ui.UiStates.playingSenderName
import com.lm.firebaseconnectapp.ui.UiStates.setPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceDuration
import com.lm.firebaseconnectapp.ui.cells.chat.message.play
import com.lm.firebaseconnectapp.ui.theme.darkGreen
import kotlin.time.Duration

@Composable
fun VoiceBar() {
    with(mainDep.recorder) {
        val onClick = with(mainDep) { remember { { getCurrentPlayTimestamp.play(recorder) } } }
        Card(
            Modifier
                .fillMaxWidth()
                .height(
                    animateDpAsState(
                        if (UiStates.getPlayerState != PlayerStates.NULL) 30.dp else 0.dp
                    ).value
                )
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.padding(start = 20.dp, top = 3.dp, bottom = 2.dp, end = 2.dp)) {
                        Icon(
                            if (UiStates.getPlayerState == PlayerStates.PAUSE ||
                                UiStates.getPlayerState == PlayerStates.NULL
                            ) Icons.Default.PlayArrow else Icons.Default.Pause,
                            null, tint = darkGreen,
                            modifier = Modifier.clickable(onClick = onClick)
                        )
                    }
                    Text(
                        text = "Sender: ${playingSenderName.value} / Time: ${playingSendTime.value}",
                        fontSize = 12.sp
                    )
                }
                Box {
                    Icon(Icons.Default.Close, null, modifier =
                    Modifier.clickable { stopPlay(); setPlayerState(PlayerStates.NULL)
                        setVoiceDuration(Duration.ZERO)
                    }
                        .padding(end = 20.dp, top = 3.dp)
                    )
                }
            }
        }
    }
}