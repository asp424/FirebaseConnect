package com.lm.firebaseconnectapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.States.notifyState
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.RecordState
import com.lm.firebaseconnectapp.record_sound.Recorder
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.setRecordState
import com.lm.firebaseconnectapp.ui.cells.ChatMessage
import com.lm.firebaseconnectapp.ui.cells.getChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun ChatScreen() {
    with(mainDep) {
        with(firebaseConnect) {
            val userModel by remember {
                listMessages.value = UIMessagesStates.Loading
                derivedStateOf {
                    sPreferences.getChatModel(firebaseConnect)
                }
            }
            val coroutine = rememberCoroutineScope()
            LaunchedEffect(userModel.id) {
                delay(300)
                firebaseRead.startListener()
            }

            val keyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect(get) {
                if (INCOMING_CALL.isType || OUTGOING_CALL.isType) {
                    keyboardController?.hide()
                }
            }
            SetChatContent {
                val context = LocalContext.current
                Scaffold {
                    if (listMessages.value is UIMessagesStates.Success) {
                        val listMessages =
                            (listMessages.value as UIMessagesStates.Success).list
                        val state = rememberLazyListState(listMessages.size)

                        LaunchedEffect(listMessages) {
                            if (listMessages.isNotEmpty()) {
                                if (listMessages[listMessages.lastIndex].second == "green")
                                    state.scrollToItem(listMessages.size)
                            }
                        }
                        var text by remember { mutableStateOf("") }

                        val sendButtonClick = remember {
                            {
                                if (text.isNotEmpty()) {
                                    sendMessage(text)
                                    text = ""
                                }
                                setNoWriting()
                            }
                        }

                        LazyColumn(
                            state = state,
                            content = {
                                items(
                                    count = listMessages.size,
                                    key = { i ->
                                        derivedStateOf { listMessages[i].first.messageKey() }
                                            .value
                                    },
                                    itemContent = { i -> ChatMessage(listMessages, i) }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                bottom = 100.dp, start = 10.dp, end = 10.dp, top = 10.dp
                            )
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
                                            if (userModel.isWriting) 0.dp else (-100).dp
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
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                TextField(
                                    value = text,
                                    onValueChange = {
                                        text = it
                                        if (it.isNotEmpty()) setWriting() else setNoWriting()
                                    },
                                    modifier = Modifier.width(width - 100.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors =
                                    TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Transparent,
                                        unfocusedIndicatorColor = Transparent,
                                        disabledIndicatorColor = Transparent
                                    )
                                )
                                FloatingActionButton(
                                    onClick = sendButtonClick,
                                    containerColor = if (isSystemInDarkTheme()) White else getMainColor,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .offset(0.dp, (-6).dp)
                                ) {
                                    Icon(
                                        Icons.Default.Send, null,
                                        tint = if (isSystemInDarkTheme()) Black else getSecondColor
                                    )
                                }
                                var isWork = remember {
                                    mutableStateOf(true)
                                }
                                FloatingActionButton(
                                    onClick = {},
                                    containerColor = if (isSystemInDarkTheme()) White else getMainColor,
                                    modifier = Modifier
                                        .motionEventSpy {
                                            val clickJob = clickTimer(isWork, recorder)
                                            if (it.action == 1) {
                                                isWork.value = false
                                                if (getRecordState == RecordState.RECORDING) {
                                                    recorder.stopRecord()
                                                        ?.apply {
                                                            sendMessage(
                                                                "$IS_RECORD${this}"
                                                            )
                                                            firebaseStorage.saveSound(
                                                                context, this
                                                            ) { res ->
                                                                if (res.isNotEmpty())
                                                                    context.showToast(res)
                                                            }
                                                        }
                                                    setRecordState(RecordState.NULL)
                                                }
                                            } else {
                                                isWork.value = true
                                                clickJob.start()
                                            }
                                        }
                                        .size(46.dp)
                                        .offset(0.dp, (-6).dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Mic, null,
                                        tint = if (isSystemInDarkTheme()) Black else getSecondColor
                                    )
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
                                        animateFloatAsState(if (notifyState.value) 1f else 0f).value
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
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(
                            animScale(
                                getRecordState == RecordState.RECORDING
                            )
                        )
                        .fillMaxSize()
                ) {
                    FloatingActionButton(
                        onClick = {}, modifier =
                        Modifier.size(100.dp)
                    ) {
                        if (getRecordState == RecordState.SAVING) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                Icons.Rounded.Mic, null,
                                tint = Red, modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun clickTimer(isWork: MutableState<Boolean>, recorder: Recorder)
= CoroutineScope(IO).launch(start = CoroutineStart.LAZY) {
    (0 .. 10).asFlow().collect {
        if (isWork.value) {
            delay(20)
            it.log
            if (it == 10) {
                "ass".log
                isWork.value = false
                recorder.startRecord()
            }
        }
    }
}

