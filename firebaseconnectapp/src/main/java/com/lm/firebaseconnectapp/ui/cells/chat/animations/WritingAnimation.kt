package com.lm.firebaseconnectapp.ui.cells.chat

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Boolean.WritingAnimation() {

    Card(
        Modifier
            .padding(20.dp, bottom = 2.dp)
            .offset(animateDpAsState(if (this) 0.dp else (-100).dp).value), CircleShape
    ) {
        Text(
            "writing...",
            Modifier.padding(5.dp)
        )
    }
}