package com.lm.firebaseconnect.listeners

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lm.firebaseconnect.FirebaseSave
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteLoadStates
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking

class ValueEventListenerInstance(
    private val firebaseSave: FirebaseSave
) {

    suspend fun ProducerScope<RemoteLoadStates>.eventListener() {
        listener.apply {
            with(firebaseSave)
            {
                firebaseConnect.chatId.messagesPath.addValueEventListener(this@apply)
                awaitClose {
                    firebaseConnect.chatId
                        .messagesPath.removeEventListener(this@apply);"stopMessages".log
                }
            }
        }
    }

    private val ProducerScope<RemoteLoadStates>.listener
        get() =
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    trySendBlocking(RemoteLoadStates.Success(snapshot))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(RemoteLoadStates.Failure(error))
                }
            }
    private val String.messagesPath
        get() = with(firebaseSave) {
            databaseReference
                .child(Nodes.CHATS.node())
                .child(getPairPath)
        }
}