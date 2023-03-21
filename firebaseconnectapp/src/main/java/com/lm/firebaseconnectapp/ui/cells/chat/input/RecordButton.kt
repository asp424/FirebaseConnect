package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.FirebaseConnect.Companion.TWO
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.setUnreadIndex
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecordButton() {
    val haptic = LocalHapticFeedback.current
    var isMustSend by remember { mutableStateOf(false) }
    with(mainDep) {
        with(firebaseConnect) {

            FloatingActionButton({},
                Modifier
                    .motionEventSpy {
                        if (it.action == 1 && isMustSend) {
                            recorder.stopRecord()
                            setNoWriting()
                        }
//                        if (it.action == 2) {
//                            isMustSend = false
//                            recorder.stopRecordWithoutSend()
//                            setNoWriting()
//                        }
                    }
                    .size(46.dp)
                    .padding(start = 2.dp)
                    .offset(0.dp, (-6).dp),
                containerColor = if (isSystemInDarkTheme()) White else getMainColor
            ) {
                Icon(
                    Icons.Outlined.Mic, null, Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
                                isMustSend = true
                                recorder.startRecord()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                setWriting(TWO)
                                setUnreadIndex(-1)
                            })
                        }, if (isSystemInDarkTheme()) Black else getSecondColor
                )
            }
        }
    }
}



