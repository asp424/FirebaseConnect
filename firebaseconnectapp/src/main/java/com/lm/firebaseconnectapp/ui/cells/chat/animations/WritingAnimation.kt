package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.AnimDp
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible

@Composable
fun String.WritingAnimation() {
    var x by remember { mutableStateOf(0.dp) }
    var y by remember { mutableStateOf(0.dp) }

    AnimDp(this != "0", first = 0.dp, second = (-100).dp) { x = it }
    AnimDp(getReplyVisible, first = 0.dp, second = 40.dp) { y = it }
    Card(
        Modifier
            .padding(20.dp, bottom = 2.dp)
            .offset(x, y), CircleShape
    ) {
        Text(
            when (this@WritingAnimation) {
                "0" -> ""
                "1" -> "writing..."
                "2" -> "recording voice..."
                else -> ""
            },
            Modifier.padding(5.dp)
        )
    }
}