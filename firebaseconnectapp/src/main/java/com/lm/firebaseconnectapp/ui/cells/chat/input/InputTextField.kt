package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.inputText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextField() {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(getReplyVisible){
        if (getReplyVisible) focusRequester.requestFocus()
    }

    with(mainDep.firebaseConnect) {
        TextField(
            inputText.value, {
                inputText.value = it
                if (it.isNotEmpty()) setWriting() else setNoWriting()
            },
            Modifier
                .width(width - 100.dp)
                .padding(end = 3.dp)
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(30.dp),
            colors =
            TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}