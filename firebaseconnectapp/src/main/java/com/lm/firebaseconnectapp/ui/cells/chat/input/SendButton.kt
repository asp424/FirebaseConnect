package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates

@Composable
fun MutableState<String>.SendButton() {

    val sendButtonClick = with(mainDep.firebaseConnect) {
        remember {
            {
                if (value.isNotEmpty()) sendMessage(value)
                value = ""
                setNoWriting()
            }
        }
    }

    FloatingActionButton(
        sendButtonClick,
        Modifier
            .size(46.dp)
            .padding(end = 1.dp)
            .offset(0.dp, (-6).dp),
        containerColor =
        if (isSystemInDarkTheme()) Color.White else UiStates.getMainColor,
    ) {
        Icon(
            Icons.Default.Send, null,
            tint = if (isSystemInDarkTheme()) Color.Black else UiStates.getSecondColor
        )
    }
}