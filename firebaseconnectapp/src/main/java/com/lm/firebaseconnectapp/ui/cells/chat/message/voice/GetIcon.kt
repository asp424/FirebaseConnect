package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.ui.UiStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun String.GetIcon(onSet: (ImageVector) -> Unit) =
    LaunchedEffect(UiStates.getPlayerState, UiStates.getCurrentPlayTimestamp) {
        withContext(Dispatchers.IO) {
            onSet(
                if (this@GetIcon == UiStates.getCurrentPlayTimestamp) {
                    if (UiStates.getPlayerState == PlayerStates.PAUSE ||
                        UiStates.getPlayerState == PlayerStates.NULL
                    ) Icons.Default.PlayArrow else Icons.Default.Pause
                } else Icons.Default.PlayArrow
            )
        }
    }
