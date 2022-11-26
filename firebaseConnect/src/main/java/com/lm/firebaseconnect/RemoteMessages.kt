package com.lm.firebaseconnect

import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.CALL
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.CHECK_FOR_CALL
import com.lm.firebaseconnect.States.DESTINATION_ID
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.REJECTED_CALL
import com.lm.firebaseconnect.States.TITLE
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.TYPE_MESSAGE
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteMessageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class RemoteMessages(
    val firebaseRead: FirebaseRead, private val fcmProvider: FCMProvider
) {

    fun message(message: String) {
        sendRemoteMessage("Сообщение от $getMyName", message, MESSAGE)
    }

    fun initialCall() {
        firebaseRead.firebaseSave.save(CALL, Nodes.CALL, digit = getChatId) {
            remoteMessageModel.setOutgoingCall
            firebaseRead.readNode(Nodes.TOKEN, getMyDigit) { myToken ->
                firebaseRead.readNode(Nodes.TOKEN, getChatId) { token ->
                    fcmProvider.send(
                        JSONObject()
                            .put(TYPE_MESSAGE, CHECK_FOR_CALL)
                            .put(DESTINATION_ID, getChatId)
                            .put(CALLING_ID, getMyDigit)
                            .put(TOKEN, myToken), token
                    )
                }
            }
        }
    }

    fun cancel(id: String) {
        rejectCall
        with(firebaseRead.firebaseSave) {
            save(REJECTED_CALL, Nodes.CALL, path = id.getPairPath, digit = id)
        }
    }

    fun checkForCall(model: RemoteMessageModel) = with(model) {
        with(firebaseRead.firebaseSave) {
            firebaseRead.readNode(
                Nodes.CALL, destinationId, getPairPathFromRemoteMessage(destinationId, callingId)
            ) { callState ->
                if (callState == CALL) {
                    if (WAIT.isType) {
                        callMessage(GET_CHECK_FOR_CALL, destinationId, token)
                        remoteMessageModel.checkForCall
                    } else callMessage(BUSY, destinationId, token)
                }
            }
        }
    }

    fun doCall() {
        CoroutineScope(IO).launch {
            sendRemoteMessage("Входящий вызов", "", INCOMING_CALL)
            delay(600)
            remoteMessageModel.getIncomingCall.set
        }
    }

    val rejectCall get() = remoteMessageModel.rejectCall.set

    private val getMyName get() = firebaseRead.firebaseSave.firebaseConnect.myName

    private val getMyIcon get() = firebaseRead.firebaseSave.firebaseConnect.myIcon

    val getMyDigit get() = firebaseRead.firebaseSave.firebaseConnect.myDigit

    private val getChatId get() = firebaseRead.firebaseSave.firebaseConnect.chatId

    private fun sendRemoteMessage(
        title: String,
        message: String,
        typeMessage: String,
        callingId: String = getMyDigit,
        destinationId: String = getChatId
    ) {
        firebaseRead.readNode(Nodes.TOKEN, callingId) { myToken ->
            firebaseRead.readNode(Nodes.TOKEN, destinationId) { token ->
                fcmProvider.send(
                    JSONObject()
                        .put(TYPE_MESSAGE, typeMessage)
                        .put(TOKEN, myToken)
                        .put(CALLING_ID, callingId)
                        .put(DESTINATION_ID, destinationId)
                        .put(MESSAGE, message)
                        .put(TITLE, title)
                        .put(NAME, getMyName)
                        .put(ICON, getMyIcon), token
                )
            }
        }
    }

    fun cancelCall(typeMessage: String, token: String, destinationId: String, callingId: String) {
        fcmProvider.send(cancelInbox(typeMessage, destinationId, callingId), token)
        rejectCall
    }

    private fun cancelInbox(typeMessage: String, destinationId: String, callingId: String) =
        JSONObject().put(TYPE_MESSAGE, typeMessage).put(DESTINATION_ID, destinationId)
            .put(CALLING_ID, callingId).put(NAME, getMyName).put(ICON, getMyIcon)

    fun callMessage(typeMessage: String, destinationId: String, token: String) =
        fcmProvider.send(
            JSONObject()
                .put(TYPE_MESSAGE, typeMessage)
                .put(DESTINATION_ID, destinationId), token
        )
}
