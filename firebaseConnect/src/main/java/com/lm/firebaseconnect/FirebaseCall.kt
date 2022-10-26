package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.WAIT

internal class FirebaseCall(
    private val firebaseSave: FirebaseSave,
    private val firebaseRead: FirebaseRead
) {
    fun call(remoteMessages: RemoteMessages) {
        firebaseRead.startReadCall {
           // if (it == WAIT) {
                callState.value = remoteMessageModel.outgoingCall()
                remoteMessages.call()
                firebaseSave.saveBusy()
          //  }
        }
    }
}