package com.lm.firebaseconnect

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.lm.firebaseconnect.models.RemoteMessageModel
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnect.models.UIUsersStates

object State {
    val onLineState = mutableStateOf(false)

    var writingState = mutableStateOf(false)

    var notifyState = mutableStateOf(false)

    var callState = mutableStateOf(RemoteMessageModel())

    val remoteMessageModel by lazy { RemoteMessageModel.Instance() }

    val listMessages: MutableState<UIMessagesStates> = mutableStateOf(UIMessagesStates.Loading)

    @SuppressLint("MutableCollectionMutableState")
    var listUsers: MutableState<UIUsersStates> = mutableStateOf(UIUsersStates.Loading)
    const val TOKEN = "registration_ids"
    const val DATA = "data"
    const val NAME = "name"
    const val TYPE_MESSAGE = "typeMessage"
    const val INCOMING_CALL = "incomingCall"
    const val OUTGOING_CALL = "outgoingCall"
    const val ANSWER = "answer"
    const val REJECT = "reject"
    const val RESET = "reset"
    const val MESSAGE = "message"
    const val WAIT = "wait"
    const val BUSY = "busy"
    const val GET_INCOMING_CALL = "getIncomingCall"
    const val CALLING_ID = "callingId"
    const val GET_CHECK_FOR_CALL = "getCheckForCall"
    const val CHECK_FOR_CALL = "checkForCall"
}
