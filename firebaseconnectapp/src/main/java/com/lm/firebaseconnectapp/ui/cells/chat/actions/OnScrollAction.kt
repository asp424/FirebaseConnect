package com.lm.firebaseconnectapp.ui.cells.chat.actions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getDateCardVisible
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceBarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setCurrentDateAtScroll
import com.lm.firebaseconnectapp.ui.UiStates.setDateCardVisible
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedMutableState")
@Composable
fun List<MessageModel>.OnScrollAction(state: LazyListState) {

    LaunchedEffect(state.isScrollInProgress) {
        snapshotFlow { state.firstVisibleItemIndex }.collect {
            if (state.isScrollInProgress) {
                if (it != 0) {
                    setDateCardVisible(true)
                    setCurrentDateAtScroll(get(it).date)
                } else {
                    if (getDateCardVisible) setDateCardVisible(false)
                }
            } else {
                delay(700)
                setDateCardVisible(false)
            }
        }
    }
}
