package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.TypeMessage
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.setUnreadIndex
import com.lm.firebaseconnectapp.ui.cells.chat.animations.DateAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.animations.ReplyAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.cells.NewMessageBox
import com.lm.firebaseconnectapp.ui.cells.chat.cells.SwipeAbleBox
import com.lm.firebaseconnectapp.ui.cells.chat.message.text.TextMessage
import com.lm.firebaseconnectapp.ui.cells.chat.message.text.Time
import com.lm.firebaseconnectapp.ui.cells.chat.message.voice.VoiceMessage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun Message(
    state: LazyListState,
    i: Int, size: Int, messageModel: MessageModel, listIsNotEmpty: Boolean
) {
    with(messageModel) {
        var index by remember { mutableStateOf(0) }
        var timeSize by remember { mutableStateOf(IntSize.Zero) }

        LaunchedEffect(state) {
            snapshotFlow { state.firstVisibleItemIndex }.collect {
                index = it
            }
        }

        with(mainDep) {
            LaunchedEffect(true) {
                setWasRead(firebaseConnect)
                if (isUnreadFlag) {
                    setUnreadIndex(i)
                    if (listIsNotEmpty) state.animateScrollToItem(size)
                }
            }
        }

        if (isNewDate) DateAnimation(true, date, 10.dp, index)

        NewMessageBox(i)

        SwipeAbleBox(state) { offset ->
            ReplyAnimation(offset)
            Card(
                Modifier
                    .padding(2.dp)
                    .offset { IntOffset(offset.roundToInt(), 0) }
                    .widthIn(0.dp, 250.dp)
                    .onGloballyPositioned { timeSize = it.size },
                shape = RoundedCornerShape(topStartShape, 20.dp, bottomEndShape, 20.dp),
                colors = CardDefaults.cardColors(wasReadColor.copy(alpha = 0.1f))
            ) {
                ReplyBox()
                Box {
                    LocalDensity.current.apply {
                    if (type == TypeMessage.VOICE) VoiceMessage() else TextMessage()
                         Time(DpSize(timeSize.width.toDp(), timeSize.height.toDp()))
                    }
                }
            }
        }
    }
}

suspend fun MessageModel.setWasRead(firebaseConnect: FirebaseConnect) {
    if (mustSetWasRead)
        withContext(IO) { with(firebaseConnect.firebaseRead.firebaseSave) { setWasRead() } }
}

