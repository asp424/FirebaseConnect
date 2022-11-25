package com.lm.firebaseconnectapp.ui.cells.chat.actions

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import com.lm.firebaseconnectapp.ui.UiStates.keyboardHeight
import kotlinx.coroutines.delay

@Composable
fun OnKeyboardAction(
    isKeyboardOpen: State<Boolean>,
    state: LazyListState
) {
    var height = remember { 0f }
    LocalDensity.current.apply {
        LaunchedEffect(isKeyboardOpen) {
            snapshotFlow { isKeyboardOpen.value }.collect {
                if (isKeyboardOpen.value) {
                    delay(100)
                    state.animateScrollBy(keyboardHeight.value.toFloat().apply { height = -this })
                }
                else state.animateScrollBy(height)
            }
        }
    }
}





