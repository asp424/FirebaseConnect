package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.RING
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE

internal class FirebaseMessageServiceChatCallback() {

    fun sendCallBack(chatPath: String, chatId: String, typeMessage: String) {
        if (typeMessage == MESSAGE) {
            databaseReference.child(chatId).child(Nodes.NOTIFY.node())
                .updateChildren(mapOf(chatPath to RING))
        }
        if (typeMessage == INCOMING_CALL) {
            databaseReference.child(chatId).child(Nodes.CALL.node())
                .updateChildren(mapOf(chatPath to INCOMING_CALL))
        }
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}