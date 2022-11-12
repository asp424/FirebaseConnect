package com.lm.firebaseconnectapp.ui.cells.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.notifyState
import com.lm.firebaseconnectapp.animScale

@Composable
fun NotificationAnimation() {

    Column(
        Modifier
            .fillMaxSize()
            .padding(end = 20.dp, bottom = 60.dp), Arrangement.Bottom, Alignment.End
    ) {
        Icon(
            Icons.Rounded.Notifications, null,
            Modifier
                .size(40.dp)
                .scale(animScale(notifyState.value)),
            Color.Green,
        )
    }
}