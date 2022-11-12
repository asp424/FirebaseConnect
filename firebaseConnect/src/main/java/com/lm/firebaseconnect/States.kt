package com.lm.firebaseconnect

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.lm.firebaseconnect.models.RemoteMessageModel
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnect.models.UIUsersStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object States {
    val onLineState = mutableStateOf(false)

    var writingState = mutableStateOf(false)

    var notifyState = mutableStateOf(false)


    private var callState = mutableStateOf(RemoteMessageModel())

    val remoteMessageModel by lazy { RemoteMessageModel.Instance() }

    val listMessages: MutableState<UIMessagesStates> = mutableStateOf(UIMessagesStates.Loading)

    val RemoteMessageModel.set get() = run { callState.value = this }

    val String.isType get() = callState.value.typeMessage == this

    val callScreenVisible = mutableStateOf(false)

    @Composable
    fun StateController() = LaunchedEffect(get){
        callScreenVisibleController()
    }



    val isOutgoingCall get() =
        OUTGOING_CALL.isType|| GET_INCOMING_CALL.isType|| GET_CHECK_FOR_CALL.isType

    private suspend fun callScreenVisibleController() {
        callScreenVisible.value = when (get.typeMessage) {
            OUTGOING_CALL -> true
            INCOMING_CALL -> true
            GET_CHECK_FOR_CALL -> true
            GET_INCOMING_CALL -> true
            WAIT ->{
                delay(1000)
                false
            }
            ANSWER -> {
                delay(1000)
                false
            }

            REJECT -> {
                delay(1000)
                false
            }

            RESET -> {
                delay(1000)
                false
            }
            BUSY -> true
            else -> false
        }
    }

    fun getCallingState() = when(get.typeMessage){

        REJECT -> "Вызов отменен"

        ANSWER -> "Открывается jitsi..."

        RESET -> "Вызов отменен"

        GET_CHECK_FOR_CALL -> "Соединение установлено"

        INCOMING_CALL -> "Входящий вызов"

        GET_INCOMING_CALL -> "Идёт вызов..."

        BUSY -> "Юзер занят"

        WAIT -> "Вызов отменен"

        OUTGOING_CALL -> "Установка соединения..."
        else -> ""
    }

    val getToken get() = callState.value.token

    val get get() = callState.value

    @SuppressLint("MutableCollectionMutableState")
    var listUsers: MutableState<UIUsersStates> = mutableStateOf(UIUsersStates.Loading)

    const val TOKEN = "registration_ids"
    const val DATA = "data"
    const val NAME = "name"
    const val ICON = "icon"
    const val TITLE = "title"
    const val TYPE_MESSAGE = "typeMessage"
    const val INCOMING_CALL = "incomingCall"
    const val OUTGOING_CALL = "outgoingCall"
    const val CALL = "call"
    const val CALL_STATE = "callState"
    const val ANSWER = "answer"
    const val REJECT = "reject"
    const val RESET = "reset"
    const val MESSAGE = "message"
    const val WAIT = "wait"
    const val BUSY = "busy"
    const val GET_INCOMING_CALL = "getIncomingCall"
    const val CALLING_ID = "callingId"
    const val DESTINATION_ID = "destinationId"
    const val GET_CHECK_FOR_CALL = "getCheckForCall"
    const val CHECK_FOR_CALL = "checkForCall"
    const val NOTIFY_CALLBACK = "notifyCallback"
    const val REJECTED_CALL = "rejectedCall"
}
