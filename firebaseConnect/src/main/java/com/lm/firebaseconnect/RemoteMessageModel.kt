package com.lm.firebaseconnect

import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.State.API_KEY
import com.lm.firebaseconnect.State.CALLING_ID
import com.lm.firebaseconnect.State.CHAT_ID
import com.lm.firebaseconnect.State.CHAT_PATH
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.NAME
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.State.TOKEN
import com.lm.firebaseconnect.State.TYPE_MESSAGE
import com.lm.firebaseconnect.State.WAIT

data class RemoteMessageModel constructor(
    val typeMessage: String = WAIT,
    val chatPath: String = "",
    val chatId: String = "",
    val textMessage: String = "",
    val name: String = "",
    val callingId: String = "",
    val token: String = "",
    val apiKey: String = ""
) {

    class Instance() {
        private fun RemoteMessage.getValue(value: String) = data[value] ?: ""
        fun getFromRemoteMessage(remoteMessage: RemoteMessage) = with(remoteMessage) {
            RemoteMessageModel(
                typeMessage = getValue(TYPE_MESSAGE),
                chatPath = getValue(CHAT_PATH),
                chatId = getValue(CHAT_ID),
                textMessage = getValue(MESSAGE),
                name = getValue(NAME),
                callingId = getValue(CALLING_ID),
                token = getValue(TOKEN),
                apiKey = getValue(API_KEY),
            )
        }

        val rejectCall get() = RemoteMessageModel(WAIT)

        val outgoingCall get() = RemoteMessageModel(OUTGOING_CALL)

        val getIncomingCall get() = RemoteMessageModel(GET_INCOMING_CALL)
    }
}
