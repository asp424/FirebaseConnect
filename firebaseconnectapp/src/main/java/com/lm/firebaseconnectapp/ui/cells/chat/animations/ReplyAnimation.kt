package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor

@Composable
fun MessageModel.ReplyAnimation(offset: Float) =

    Row(
        Modifier
            .fillMaxWidth()
            .padding(end = bottomEndShape, start = 60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            Icons.Default.Reply, null, modifier =
            Modifier
                .scale(offset / 35)
                .rotate(180f)
                .offset(offset.dp / 10, 0.dp), tint = getMainColor
        )
    }
