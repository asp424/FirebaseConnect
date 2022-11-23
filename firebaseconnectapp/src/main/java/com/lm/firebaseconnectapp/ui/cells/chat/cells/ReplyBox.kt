package com.lm.firebaseconnectapp.ui.cells.chat.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.FirebaseRead
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.setReplyVisible
import kotlinx.coroutines.launch


@Composable
fun MessageModel.ReplyBox(state: LazyListState) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .scale(animScale(getReplyVisible))
            .background(LightGray.copy(0.5f))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 10.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Reply, null,
                modifier = Modifier.rotate(180f), tint = UiStates.getMainColor
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    name, fontWeight = FontWeight.Bold
                )
                Text(
                    if (text.contains(FirebaseRead.IS_RECORD)) {
                        "voice"
                    } else {
                        if (text.length >= 30) "${text.substring(0, 30)}..."
                        else text
                    }, maxLines = 1
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(Icons.Default.Close, null, modifier = Modifier.clickable {
                coroutineScope.launch {
                    state.animateScrollBy(-140f)
                }
                setReplyVisible(false)
            })
        }
    }
}

