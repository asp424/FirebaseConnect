package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.BUSY
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.WAIT

internal class FirebaseCall(
    private val firebaseSave: FirebaseSave,
    private val firebaseRead: FirebaseRead
) {
    fun call(remoteMessages: RemoteMessages) {
        callState.value = remoteMessageModel.outgoingCall()
        firebaseRead.startReadCall {
            if (it == WAIT) {
                setBusy()
                remoteMessages.call()
                firebaseSave.saveBusy()
            } else callState.value = remoteMessageModel.rejectCall()
        }
    }

    private fun setBusy(){
        firebaseSave.databaseReference
            .child(firebaseSave.firebaseChat.chatId)
            .child(Nodes.CALL.node())
            .updateChildren(mapOf(firebaseRead.firebaseSave.pairPath to BUSY))
    }

    private fun sendCallRequest() {
        firebaseSave.databaseReference.child(firebaseSave.firebaseChat.chatId)
            .child(Nodes.CALL.node())
            .updateChildren(mapOf(firebaseSave.pairPath to INCOMING_CALL))
    }

    fun sendReject() {
        firebaseSave.databaseReference.child(firebaseSave.firebaseChat.chatId)
            .child(Nodes.CALL.node())
            .updateChildren(mapOf(firebaseSave.pairPath to REJECT))
        callState.value = remoteMessageModel.rejectCall()
    }
}