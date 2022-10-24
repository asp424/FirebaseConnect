package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.RING
import com.lm.firebaseconnect.State.BUSY
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.WAIT

internal class FirebaseMessageServiceChatCallback() {

    fun sendCallBack(
        chatPath: String, chatId: String, typeMessage: String, callingId: String
    ) {
        if (typeMessage == MESSAGE) {
            databaseReference.child(chatId).child(Nodes.NOTIFY.node())
                .updateChildren(mapOf(chatPath to RING))
        }
        if (typeMessage == INCOMING_CALL) {
            databaseReference.child(callingId).child(Nodes.CALL.node())
                .updateChildren(mapOf(chatPath to GET_INCOMING_CALL))
        }
        if (typeMessage == REJECT) {
            databaseReference.child(chatId).child(Nodes.CALL.node())
                .updateChildren(mapOf(chatPath to WAIT))
        }
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}