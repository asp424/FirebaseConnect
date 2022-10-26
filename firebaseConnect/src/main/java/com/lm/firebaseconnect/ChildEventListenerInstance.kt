package com.lm.firebaseconnect

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking

class ChildEventListenerInstance {

    suspend fun ProducerScope<RemoteLoadStates>.childListener(path: DatabaseReference) =
        object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                trySendBlocking(RemoteLoadStates.Success(snapshot))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                trySendBlocking(RemoteLoadStates.Success(snapshot))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                trySendBlocking(RemoteLoadStates.Success(snapshot))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }.apply {
            path.addChildEventListener(this)
            awaitClose { path.removeEventListener(this); "stop".log }
        }
}