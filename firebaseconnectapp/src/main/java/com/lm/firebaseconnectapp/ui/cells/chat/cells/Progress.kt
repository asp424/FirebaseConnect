package com.lm.firebaseconnectapp.ui.cells.chat.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lm.fantasticprogress.CircleProgress
import com.lm.fantasticprogress.ProgressCircleType
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.models.UIMessagesStates

@Composable
fun Progress() =
    Box(
        Modifier.fillMaxSize(),
        Alignment.Center
    ) {
        CircleProgress(
            ProgressCircleType.Random, 0, 100,
            States.listMessages.value is UIMessagesStates.Loading
        )
    }
