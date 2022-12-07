package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.di.compose.MainDep
import com.lm.firebaseconnectapp.ui.UiStates

@Composable
fun SendFile(corner: Dp) {

    val sendButtonClick = with(MainDep.mainDep.firebaseConnect) {
        remember {
            {
                if (UiStates.inputText.value.isNotEmpty())
                    sendMessage(UiStates.inputText.value, if (UiStates.getReplyVisible) UiStates.getReplyMessage.key else "")
                UiStates.inputText.value = ""
                UiStates.setUnreadIndex(-1)
                setNoWriting()
                UiStates.setReplyVisible(false)
            }
        }
    }

    FloatingActionButton(
        sendButtonClick,
        Modifier
            .size(46.dp)
            .padding(end = 1.dp)
            .offset(-corner, (-6).dp),
        containerColor =
        if (isSystemInDarkTheme()) Color.White else UiStates.getMainColor
    ) {
        Icon(
            Icons.Default.FileUpload, null,
            tint = if (isSystemInDarkTheme()) Color.Black else UiStates.getSecondColor
        )
    }
}