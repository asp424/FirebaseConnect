package com.lm.firebaseconnectapp.ui.cells.chat.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OnCallStateAction() {

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(get) {
        if (States.INCOMING_CALL.isType || States.OUTGOING_CALL.isType) {
            withContext(IO) {
                keyboardController?.hide()
            }
        }
    }
}