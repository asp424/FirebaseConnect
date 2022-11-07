package com.lm.firebaseconnect.models

import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.DESTINATION_ID
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.TITLE
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.TYPE_MESSAGE
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.getToken
import com.lm.firebaseconnect.States.set

data class RemoteMessageModel constructor(
    val typeMessage: String = WAIT,
    val textMessage: String = "",
    val title: String = "",
    val callingId: String = "",
    val destinationId: String = "",
    val token: String = "",
    val name: String = "",
    val icon: String = "",
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
                icon = getValue(ICON),
                destinationId = getValue(DESTINATION_ID),
            )
        }

        val rejectCall get() = RemoteMessageModel(WAIT, title = "Отмена вызова",
            callingId = get.callingId, name = get.name)

        val getIncomingCall
            get() = RemoteMessageModel(
                GET_INCOMING_CALL, title = "Вызов...",
                callingId = get.callingId, token = getToken, name = get.name
            )

        val setOutgoingCall get() = RemoteMessageModel(OUTGOING_CALL).set

        val busy get() = RemoteMessageModel(BUSY, title = "Взял трубку",
            callingId = get.callingId, name = get.name)
    }
}
