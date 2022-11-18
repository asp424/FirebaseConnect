package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Boolean.GetText(onSet: (String) -> Unit) =
    LaunchedEffect(getCurrentPlayTimestamp, getVoiceDuration) {
        withContext(Dispatchers.IO) {
            onSet(
                if (this@GetText) {
                    if (getVoiceDuration.toString() == "0s") "" else getVoiceDuration.toString()
                } else ""
            )
        }
    }