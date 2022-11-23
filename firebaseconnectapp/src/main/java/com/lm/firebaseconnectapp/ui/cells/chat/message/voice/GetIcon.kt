package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

@Composable
fun String.GetIcon(onSet: (ImageVector) -> Unit) =
    LaunchedEffect(getPlayerState, getCurrentPlayTimestamp) {
        withContext(IO) {
            onSet(
                if (this@GetIcon == getCurrentPlayTimestamp) {
                    if (getPlayerState == PlayerStates.PAUSE || getPlayerState == PlayerStates.NULL
                    ) Icons.Default.PlayArrow else Icons.Default.Pause
                } else Icons.Default.PlayArrow
            )
        }
    }
