package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.BUSY
import com.lm.firebaseconnect.State.WAIT

internal class FirebaseCall(
    private val firebaseSave: FirebaseSave,
    private val firebaseRead: FirebaseRead
) {
    fun call(remoteMessages: RemoteMessages) {
        firebaseRead.readNode(Nodes.CALL) {
            if (it == WAIT) {
                callState.value = remoteMessageModel.outgoingCall
                with(firebaseSave) { save(BUSY, Nodes.CALL, myDigit) { remoteMessages.call() } }
            }
        }
    }
}