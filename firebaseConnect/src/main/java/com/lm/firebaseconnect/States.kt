package com.lm.firebaseconnect

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

val onLineState = mutableStateOf(false)

var writingState = mutableStateOf(false)

var notifyState = mutableStateOf(false)

var callState = mutableStateOf(RemoteMessageModel())

val remoteMessageModel by lazy { RemoteMessageModel.Instance() }

val listMessages: MutableState<UIMessagesStates> = mutableStateOf(UIMessagesStates.Loading)

@SuppressLint("MutableCollectionMutableState")
var listUsers: MutableState<UIUsersStates> = mutableStateOf(UIUsersStates.Loading)

object State {
    const val USER_ID = "userId"
    const val TOKEN = "registration_ids"
    const val DATA = "data"
    const val NAME = "name"
    const val RECEIVED = "received"
    const val TYPE_MESSAGE = "typeMessage"
    const val INCOMING_CALL = "incomingCall"
    const val MISSING_CALL = "missingCall"
    const val OUTGOING_CALL = "outgoingCall"
    const val ANSWER = "answer"
    const val REJECT = "reject"
    const val RESET = "reset"
    const val MESSAGE = "message"
    const val CHAT_PATH = "chatPath"
    const val CHAT_ID = "chatId"
    const val WAIT = "wait"
    const val BUSY = "busy"
    const val GET_INCOMING_CALL = "getIncomingCall"
    const val NOTIFY_CALLBACK = "notifyCallback"
    const val CALLING_ID = "callingId"
    const val API_KEY = "apiKey"
    const val ROOM = "room"
}
