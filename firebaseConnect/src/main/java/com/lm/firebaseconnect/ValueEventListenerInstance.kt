package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking

class ValueEventListenerInstance(
    private val firebaseSave: FirebaseSave
) {

    suspend fun ProducerScope<RemoteLoadStates>.eventListener() = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            trySendBlocking(RemoteLoadStates.Success(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            trySendBlocking(RemoteLoadStates.Failure(error))
        }
    }.apply {
        messagesPath.addValueEventListener(this)
        awaitClose { messagesPath.removeEventListener(this);"stopMessages".log }
    }

    private val messagesPath
        get() = firebaseSave.databaseReference
            .child(Nodes.CHATS.node()).child(firebaseSave.pairPath).child(Nodes.MESSAGES.node())
}