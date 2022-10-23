package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.REJECT

internal class FirebaseCall(
    private val firebaseSave: FirebaseSave,
    private val firebaseRead: FirebaseRead
) {
    fun call(remoteMessages: RemoteMessages) {
        remoteMessages.call()
        firebaseSave.saveBusy()
        callState.value = remoteMessageModel.outgoingCall()
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