package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.RecordState
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecordButton() {
    with(mainDep) {
        FloatingActionButton({},
            Modifier
                .motionEventSpy { if (it.action == 1) recorder.stopRecord() }
                .size(46.dp)
                .offset(0.dp, (-6).dp),
            containerColor = if (isSystemInDarkTheme()) White else getMainColor
        ) {
            Icon(
                Icons.Outlined.Mic, null, Modifier
                    .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { recorder.startRecord() })
                }, if (isSystemInDarkTheme()) Black else getSecondColor
            )
        }
    }
}


