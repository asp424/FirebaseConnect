package com.lm.firebaseconnect

internal class FirebaseCall(
    private val remoteMessages: RemoteMessages
) {
    fun call(token: String) {
        callState.value = remoteMessageModel.outgoingCall
        remoteMessages.call(token)
    }
}
