package com.lm.firebaseconnect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

val onLineState = mutableStateOf(false)

var writingState = mutableStateOf(false)

var notifyState = mutableStateOf(false)

var callState = mutableStateOf(RemoteMessageModel())

val remoteMessageModel by lazy { RemoteMessageModel.Instance() }


val listMessages: MutableState<UIStates> = mutableStateOf(UIStates.Loading)

object State {
    const val USER_ID = "userId"
    const val TOKEN = "registration_ids"
    const val DATA = "data"
    const val NAME = "name"
    const val RECEIVED = "received"
    const val TYPE_MESSAGE = "typeMessage"
    const val INCOMING_CALL = "incomingCall"
    const val OUTGOING_CALL = "outgoingCall"
    const val ANSWER = "answer"
    const val REJECT = "reject"
    const val MESSAGE = "message"
    const val CHAT_PATH = "chatPath"
    const val CHAT_ID = "chatId"
    const val WAIT = "wait"
    const val BUSY = "busy"
    const val GET_INCOMING_CALL = "getIncomingCall"
    const val CALLING_ID = "callingId"
}
