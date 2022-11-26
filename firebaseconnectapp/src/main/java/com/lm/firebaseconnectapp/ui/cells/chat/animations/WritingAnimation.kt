package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible

@Composable
fun String.WritingAnimation() {

    Card(
        Modifier
            .padding(20.dp, bottom = 2.dp)
            .offset(
                animDp(this != "0", first = 0.dp, second = (-100).dp),
                animDp(getReplyVisible, first = 0.dp, second = 40.dp)
            ), CircleShape
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