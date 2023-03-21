package com.lm.firebaseconnectapp.ui.cells.chat.input

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun keyboardListener(state: LazyListState): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    var keyboardHeight by remember { mutableStateOf(0f) }
    val view = LocalView.current
    LaunchedEffect(keyboardState.value) {
        state.animateScrollBy(if (keyboardState.value) keyboardHeight else -keyboardHeight)
    }
    LocalDensity.current.apply {

        DisposableEffect(view, true) {
            val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Rect()
                view.getWindowVisibleDisplayFrame(rect)
                val screenHeight = view.rootView.height
                val keypadHeight = screenHeight - rect.bottom
                keyboardState.value = keypadHeight > screenHeight * 0.15
                if (keyboardHeight == 0f)
                    if (keyboardState.value) keyboardHeight = keypadHeight - 44.dp.toPx()
            }
            view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

            onDispose {
                view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
            }
        }
    }

    return keyboardState
}