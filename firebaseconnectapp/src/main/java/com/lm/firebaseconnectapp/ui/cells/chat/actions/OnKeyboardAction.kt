package com.lm.firebaseconnectapp.ui.cells.chat.actions

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow

@Composable
fun OnKeyboardAction(
    isKeyboardOpen: State<Boolean>,
    state: LazyListState,
    bottomPadding: MutableState<Float>
) {
    LaunchedEffect(isKeyboardOpen) {
        snapshotFlow { isKeyboardOpen.value }.collect {
            state.animateScrollBy(
                if (it) bottomPadding.value else -bottomPadding.value
            )
        }
    }
}





