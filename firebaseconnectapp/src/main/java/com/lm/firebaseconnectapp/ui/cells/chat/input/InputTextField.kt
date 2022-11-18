package com.lm.firebaseconnectapp.ui.cells.chat.input

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MutableState<String>.InputTextField() {
    val width = LocalConfiguration.current.screenWidthDp.dp

    with(mainDep.firebaseConnect) {
        TextField(
            value, {
                value = it
                if (it.isNotEmpty()) setWriting() else setNoWriting()
            },
            Modifier.width(width - 100.dp).padding(end = 3.dp),
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