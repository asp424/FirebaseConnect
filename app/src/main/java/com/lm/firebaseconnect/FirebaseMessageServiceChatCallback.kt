package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.FirebaseRead.Companion.RING

class FirebaseMessageServiceChatCallback() {

    fun sendCallBack(remoteMessage: RemoteMessage) = with(remoteMessage.data) {
        val node = get("chatNode")?:""
        val receiverNode = get("receiverNode")?:""
        databaseReference.child(receiverNode).child(Nodes.NOTIFY.node())
            .updateChildren(mapOf(node to RING))
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}