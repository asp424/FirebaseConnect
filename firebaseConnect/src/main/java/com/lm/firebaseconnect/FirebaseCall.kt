package com.lm.firebaseconnect

internal class FirebaseCall(
    private val remoteMessages: RemoteMessages
) {
    fun call(token: String) {
        // firebaseRead.readNode(Nodes.CALL) {
        //    if (it == WAIT) {
        callState.value = remoteMessageModel.outgoingCall
        //  with(firebaseSave) {
        //      save(BUSY, Nodes.CALL, myDigit) {
        remoteMessages.call(token)
    }
}
// }
//}
// }
//}