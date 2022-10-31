package com.lm.firebaseconnect.listeners

import com.google.firebase.database.*
import com.lm.firebaseconnect.log
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import java.util.concurrent.Executors

class ChildEventListenerInstance {

    suspend fun ProducerScope<List<DataSnapshot>>.childListener() =
        object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) = send()
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = send()
            override fun onChildRemoved(snapshot: DataSnapshot) = send()
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }.apply {
            databaseReference.addChildEventListener(this)
            awaitClose { databaseReference.removeEventListener(this); "stop".log }
        }

    private fun ProducerScope<List<DataSnapshot>>.send() {
        databaseReference.get().addOnCompleteListener(Executors.newSingleThreadExecutor()) {
            if (it.isComplete) trySendBlocking(it.result.children.toList())
        }
    }
    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}