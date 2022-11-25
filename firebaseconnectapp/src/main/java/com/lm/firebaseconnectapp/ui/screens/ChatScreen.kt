package com.lm.firebaseconnectapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.States.GetListMessages
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.getChatModel
import com.lm.firebaseconnectapp.ui.UiStates.getDateCardVisible
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceBarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setDateCardVisible
import com.lm.firebaseconnectapp.ui.UiStates.setReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.setUnreadIndex
import com.lm.firebaseconnectapp.ui.cells.chat.NotificationAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.Progress
import com.lm.firebaseconnectapp.ui.cells.chat.actions.OnCallStateAction
import com.lm.firebaseconnectapp.ui.cells.chat.actions.OnIdAction
import com.lm.firebaseconnectapp.ui.cells.chat.actions.OnKeyboardAction
import com.lm.firebaseconnectapp.ui.cells.chat.actions.OnScrollAction
import com.lm.firebaseconnectapp.ui.cells.chat.animations.DateAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.animations.RecordingAnimation
import com.lm.firebaseconnectapp.ui.cells.chat.input.InputBar
import com.lm.firebaseconnectapp.ui.cells.chat.input.keyboardListener
import com.lm.firebaseconnectapp.ui.cells.chat.message.Message

@[SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState") OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
Composable]
fun ChatScreen() {
    val isKeyboardOpen = keyboardListener()
    val coroutine = rememberCoroutineScope()
    with(mainDep) {
        with(firebaseConnect) {
            val text = remember { mutableStateOf("") }

            val userModel by remember {
                listMessages.value = UIMessagesStates.Loading
                derivedStateOf { sPreferences.getChatModel(firebaseConnect) }
            }

            OnIdAction(userModel.id)

            OnCallStateAction()

            SetChatContent {
                Scaffold(
                    modifier = Modifier
                        .imePadding()
                        .navigationBarsPadding(),
                ) {
                    GetListMessages({
                        val state = rememberLazyListState(size)

                        OnScrollAction(state)

                        LaunchedEffect(this, true) {
                            if (isNotEmpty()) {
                                if (get(lastIndex).alignment == Alignment.CenterEnd)
                                    state.animateScrollToItem(size)
                            }
                        }

                        LazyColumn(
                            state = state, contentPadding = PaddingValues(
                                10.dp, 10.dp, 10.dp, animDp(getReplyVisible, 110.dp, 80.dp)
                            )
                        ) {
                            items(
                                size,
                                { get(it).key }, { get(it) }, { get(it).Message(state, it, size) }
                            )
                        }
                        OnKeyboardAction(isKeyboardOpen, state)
                        text.InputBar(userModel.isWriting, state)
                        NotificationAnimation()
                    }, { Progress() })
                }
                RecordingAnimation()
                DateAnimation()
            }
        }

        DisposableEffect(true) {
            onDispose {
                setDateCardVisible(false)
                setReplyVisible(false)
                setUnreadIndex(-1)
            }
        }
    }
}



