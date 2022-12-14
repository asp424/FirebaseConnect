package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getReplyMessage
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.inputText
import com.lm.firebaseconnectapp.ui.UiStates.setReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.setUnreadIndex

@Composable
fun SendButton(corner: Dp) {

    val sendButtonClick = with(mainDep.firebaseConnect) {
        remember {
            {
                if (inputText.value.isNotEmpty())
                    sendMessage(inputText.value, if (getReplyVisible) getReplyMessage.key else "")
                inputText.value = ""
                setUnreadIndex(-1)
                setNoWriting()
                setReplyVisible(false)
            }
        }
    }

    FloatingActionButton(
        sendButtonClick,
        Modifier
            .size(46.dp)
            .padding(end = 1.dp)
            .offset(corner, (-6).dp),
        containerColor =
        if (isSystemInDarkTheme()) White else getMainColor
    ) {
        Icon(
            Icons.Default.Send, null,
            tint = if (isSystemInDarkTheme()) Black else getSecondColor
        )
    }
}