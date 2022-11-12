package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.record_sound.RecordState
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState

@Composable
fun RecordingAnimation() =

    Box(
        Modifier
            .scale(animScale(getRecordState == RecordState.RECORDING))
            .fillMaxSize(),
        Center
    ) {
        FloatingActionButton({}, Modifier.size(100.dp)) {
            if (getRecordState == RecordState.SAVING) CircularProgressIndicator()
            else Icon(Icons.Rounded.Mic, null, Modifier.size(80.dp), Red)
        }
    }
