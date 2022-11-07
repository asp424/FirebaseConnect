package com.lm.firebaseconnectapp.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.States.notifyState
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.cells.getChatModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen() {
    val coroutine = rememberCoroutineScope()
    with(mainDep) {
        with(firebaseConnect) {
            with(sPreferences.getChatModel(firebaseConnect)) {
                val keyboardController = LocalSoftwareKeyboardController.current
                LaunchedEffect(get) {
                    if (INCOMING_CALL.isType || OUTGOING_CALL.isType) {
                        keyboardController?.hide()
                    }
                }
                SetChatContent {
                    Scaffold(content = {
                        if (listMessages.value is UIMessagesStates.Success) {
                            val listMessages = (listMessages.value as UIMessagesStates.Success).list
                            val state = rememberLazyListState()
                            LaunchedEffect(true) {
                                delay(300)
                                state.animateScrollToItem(listMessages.size)
                            }
                            var text by remember { mutableStateOf("") }

                            val click = remember {
                                {
                                    if (text.isNotEmpty()) {
                                        sendMessage(text)
                                        text = ""
                                    }
                                    setNoWriting()
                                }
                            }
                            LazyColumn(
                                content = {
                                    items(listMessages) {
                                        Text(
                                            text = it.first,
                                            modifier = Modifier
                                                .padding(bottom = 5.dp), color =
                                            if (it.second == "green") Green else {
                                                if (isSystemInDarkTheme()) White else Black
                                            }
                                        )
                                    }
                                }, modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    bottom = 100.dp, start = 10.dp, end = 10.dp, top = 10.dp
                                ), state = state
                            )

                            val width = LocalConfiguration.current.screenWidthDp.dp
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Card(
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .padding(start = 20.dp, bottom = 2.dp)
                                        .offset(
                                            animateDpAsState(
                                                if (isWriting) 0.dp else (-100).dp
                                            ).value
                                        )
                                ) {
                                    Text(
                                        text = "writing...",
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextField(value = text, onValueChange = {
                                        text = it
                                        if (it.isNotEmpty()) setWriting()
                                        else setNoWriting()
                                    }, modifier = Modifier
                                        .width(width - 100.dp)
                                        .onFocusEvent { focusState ->
                                            if (focusState.isFocused) {
                                                coroutine.launch {
                                                    delay(300)
                                                    state.animateScrollToItem(listMessages.size)
                                                }
                                            }
                                        })
                                    FloatingActionButton(onClick = click) {
                                        Icon(Icons.Default.Send, null)
                                    }
                                }
                            }
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(end = 20.dp, bottom = 60.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Icon(
                                    Icons.Rounded.Notifications, null,
                                    tint = Green,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .scale(
                                            animateFloatAsState(
                                                if (notifyState.value) 1f else 0f
                                            ).value
                                        )
                                )
                            }
                        } else {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    })
                }
            }
        }
    }
}
