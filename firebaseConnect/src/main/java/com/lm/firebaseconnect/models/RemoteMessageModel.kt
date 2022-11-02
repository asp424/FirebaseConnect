package com.lm.firebaseconnect.models

import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.TYPE_MESSAGE
import com.lm.firebaseconnect.States.WAIT

data class RemoteMessageModel constructor(
    val typeMessage: String = WAIT,
    val textMessage: String = "",
    val name: String = "",
    val callingId: String = "",
    val token: String = "",
) {

    class Instance {
        private fun RemoteMessage.getValue(value: String) = data[value] ?: ""
        fun getFromRemoteMessage(remoteMessage: RemoteMessage) = with(remoteMessage) {
            RemoteMessageModel(
                typeMessage = getValue(TYPE_MESSAGE),
                textMessage = getValue(MESSAGE),
                name = getValue(NAME),
                callingId = getValue(CALLING_ID),
                token = getValue(TOKEN),
            )
        }

        val rejectCall get() = RemoteMessageModel(WAIT, name = "Отмена вызова")

        val getIncomingCall get() = RemoteMessageModel(GET_INCOMING_CALL, name = "Вызов...")

        val outgoingCall get() = RemoteMessageModel(OUTGOING_CALL, name = "Отправка запроса...")

       // val busy get() = RemoteMessageModel(BUSY, name = "Взял трубку")

        val testBusy get() = RemoteMessageModel(WAIT, name = "Взял трубку")
    }
}
