package com.lm.firebaseconnectapp.ui.cells.chat.actions

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getDateCardVisible
import com.lm.firebaseconnectapp.ui.UiStates.setCurrentDateAtScroll
import com.lm.firebaseconnectapp.ui.UiStates.setDateCardVisible
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@SuppressLint("UnrememberedMutableState")
@Composable
fun List<MessageModel>.OnScrollAction(listState: LazyListState) =
    LaunchedEffect(listState.isScrollInProgress) {
        withContext(IO) {
            snapshotFlow { listState.firstVisibleItemIndex }.collect {
                if (listState.isScrollInProgress) {
                    if (it != 0 && it < lastIndex) {
                        setCurrentDateAtScroll(get(it).date)
                        delay(500)
                        setDateCardVisible(true)
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

