package com.lm.firebaseconnect.models

import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.TITLE
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.TYPE_MESSAGE
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.getToken

data class RemoteMessageModel constructor(
    val typeMessage: String = WAIT,
    val textMessage: String = "",
    val title: String = "",
    val callingId: String = "",
    val token: String = "",
    val name: String = "",
) {

    class Instance {
        private fun RemoteMessage.getValue(value: String) = data[value] ?: ""
        fun getFromRemoteMessage(remoteMessage: RemoteMessage) = with(remoteMessage) {
            RemoteMessageModel(
                typeMessage = getValue(TYPE_MESSAGE),
                textMessage = getValue(MESSAGE),
                title = getValue(TITLE),
                callingId = getValue(CALLING_ID),
                token = getValue(TOKEN),
                name = getValue(NAME),
            )
        }

        val rejectCall get() = RemoteMessageModel(WAIT, title = "Отмена вызова",
            callingId = get.callingId, name = get.name)

        val getIncomingCall
            get() = RemoteMessageModel(
                GET_INCOMING_CALL, title = "Вызов...",
                callingId = get.callingId, token = getToken, name = get.name
            )

        fun outgoingCall(id: String, name: String, token: String) = RemoteMessageModel(
            OUTGOING_CALL, title = "Отправка запроса...", name = name, token = token,
            callingId = id
        )

        // val busy get() = RemoteMessageModel(BUSY, name = "Взял трубку")

        val testBusy get() = RemoteMessageModel(WAIT, title = "Взял трубку",
            callingId = get.callingId, name = get.name)
    }
}
