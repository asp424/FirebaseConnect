package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.FirebaseRead.Companion.IS_RECORD
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.TypeMessage
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
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
fun MessageModel.Message(state: LazyListState, i: Int, size: Int) {

    var index by remember { mutableStateOf(0) }
    var timeSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(state) { snapshotFlow { state.firstVisibleItemIndex }.collect { index = it } }

    with(mainDep) { LaunchedEffect(true) { setWasRead(firebaseConnect) } }

    if (isNewDate) DateAnimation(true, date, if (index == 0) 0.dp else 10.dp)

    LaunchedEffect(true) {
        if (isUnreadFlag) {
            setUnreadIndex(i)
            if (size != 0) state.animateScrollToItem(size)
        }
    }

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
            if (isReply) {
                Canvas(Modifier) {
                    drawLine(
                        start = Offset(55f, 45f), end = Offset(55f, 130f),
                        color = getSecondColor,
                        strokeWidth = 5f
                    )
                }
                Column(modifier = Modifier.padding(start = 25.dp, top = 10.dp)) {
                    Text(replyName, fontWeight = Bold, fontSize = 14.sp, color = getSecondColor)
                    Text(
                        if (!replyText.contains(IS_RECORD)) {
                            if (replyText.length >= 10)
                                "${replyText.substring(0, 10)}..." else replyText
                        } else "voice", maxLines = 1,
                        fontSize = 12.sp
                    )
                }
            }
            Box {
                if (type == TypeMessage.VOICE) VoiceMessage() else TextMessage()
                Time(timeSize)
            }
        }
    }
}

suspend fun MessageModel.setWasRead(firebaseConnect: FirebaseConnect) {
    if (mustSetWasRead)
        withContext(IO) { with(firebaseConnect.firebaseRead.firebaseSave) { setWasRead() } }
}

