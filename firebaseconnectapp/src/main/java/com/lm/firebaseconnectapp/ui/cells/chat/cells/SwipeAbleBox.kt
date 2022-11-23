package com.lm.firebaseconnectapp.ui.cells.chat.cells

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.setReplyMessage
import com.lm.firebaseconnectapp.ui.UiStates.setReplyVisible
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MessageModel.SwipeAbleBox(state: LazyListState, offset: @Composable BoxScope.(Float) -> Unit) {
    val keyBoardController = LocalSoftwareKeyboardController.current
    val swipeAbleState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, with(LocalDensity.current) { 500.dp.toPx() } to 1)
    val vibration = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .motionEventSpy {
                if (it.action == 1 && swipeAbleState.offset.value < -20f) {
                    vibration.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (!getReplyVisible)
                        coroutineScope.launch {
                            keyBoardController?.show()
                            state.animateScrollBy(140f)
                        }
                    setReplyMessage(this)
                    setReplyVisible(true)
                }
            }
            .swipeable(
                swipeAbleState,
                anchors, Orientation.Horizontal,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                velocityThreshold = 20.dp
            ), contentAlignment = alignment
    ) { offset(this, if (swipeAbleState.offset.value < 0) swipeAbleState.offset.value else 0f) }
}
