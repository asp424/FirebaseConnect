package com.lm.firebaseconnectapp.ui.cells.chat.message.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlin.time.Duration

@Composable
fun Boolean.GetText(onSet: (String) -> Unit) =
    LaunchedEffect(getCurrentPlayTimestamp, getVoiceDuration) {
            if (this@GetText) {
                if (getVoiceDuration == Duration.ZERO) onSet("")
                else onSet(getVoiceDuration.toString())
        } else onSet("")
    }