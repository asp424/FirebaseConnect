package com.lm.firebaseconnectapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement.Bottom
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.cells.chat.NotificationAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.WritingAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.animations.RecordingAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.input.InputTextField
import com.lm.firebaseconnectapp.ui.cells.chat.input.KeyboardListener
import com.lm.firebaseconnectapp.ui.cells.chat.input.RecordButton
import com.lm.firebaseconnectapp.ui.cells.chat.input.SendButton
import com.lm.firebaseconnectapp.ui.cells.chat.message.Message
import com.lm.firebaseconnectapp.ui.cells.getChatModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun ChatScreen() {
    val isKeyboardOpen by KeyboardListener()
    with(mainDep) {
        with(firebaseConnect) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val text = remember { mutableStateOf("") }

            val userModel by remember {
                listMessages.value = UIMessagesStates.Loading
                derivedStateOf { sPreferences.getChatModel(firebaseConnect) }
            }

            LaunchedEffect(userModel.id) {
                delay(700)
                firebaseRead.startListener()
            }

            LaunchedEffect(get) {
                if (INCOMING_CALL.isType || OUTGOING_CALL.isType) {
                    keyboardController?.hide()
                }
            }
            val coroutine = rememberCoroutineScope()
            SetChatContent {
                Scaffold {
                    if (listMessages.value is UIMessagesStates.Success) {

                        (listMessages.value as UIMessagesStates.Success).list.apply {
                            val state = rememberLazyListState(size)

                            LaunchedEffect(this) {
                                state.animateScrollToItem(size)
                            }

                            var y by remember {
                                mutableStateOf(0f)
                            }
                            var offset by remember {
                                mutableStateOf(0f)
                            }
                            var scrollState by remember {
                                mutableStateOf(0f)
                            }
                            LocalDensity.current.apply {
                                TrickyHeight {

                                }
                                LazyColumn(
                                    Modifier
                                        .fillMaxWidth(), state, PaddingValues(
                                        10.dp, 10.dp, 10.dp, 80.dp
                                    )
                                ) {
                                    items(
                                        size,
                                        { derivedStateOf { get(it).key }.value },
                                        itemContent = {
                                            get(it).Message()
                                        }
                                    )
                                }

                                Column(Modifier.fillMaxSize(), Bottom, Alignment.Start) {

                                    userModel.isWriting.WritingAnimation()
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                        ,
                                        Center, Alignment.Bottom
                                    ) {
                                        text.InputTextField()
                                        text.SendButton()
                                        RecordButton()
                                    }
                                }

                                NotificationAnimation()
                            }
                        }
                    } else
                        Column(Modifier.fillMaxSize(), Center, CenterHorizontally) {
                            CircularProgressIndicator()
                        }
                }
                RecordingAnimation()
            }
        }
    }
}

@Composable
private fun TrickyHeight(
    onHeightChanged: (Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .onSizeChanged {
                onHeightChanged(
                    it.height.toFloat()
                )
            }
    )
}


