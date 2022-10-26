package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseHandler(
    private val firebaseConnect: FirebaseConnect,
    private val firebaseSave: FirebaseSave,
    private val childEventListenerInstance: ChildEventListenerInstance
) {

    fun startMainListener() {
        stopMainListener()
        job = CoroutineScope(IO).launch {
            listener().collect {
                databaseReference.get().addOnCompleteListener { t ->
                    listUsers.value = UIUsersStates.Success(t.result.children.map { it }.filter())
                }
            }
        }
    }

    fun stopMainListener() = job.cancel()

    private fun List<DataSnapshot>.filter() = filter { it.key != firebaseConnect.myDigit }
        .filter { it.key != Nodes.CHATS.node() }
        .map {
            it.getUserModel(it.key?.pairPath, firebaseConnect.chatId) {
                firebaseSave.clearHimNotify()
            }
        }

    var job: Job = Job()

    private fun listener() = callbackFlow {
        with(childEventListenerInstance) { childListener(databaseReference) }
    }

    private val String.pairPath get() = "${FIRST_USER_START}${
            maxOf(firebaseConnect.myDigit, this)
        }${FIRST_USER_END}${SECOND_USER_START}${
            minOf(firebaseConnect.myDigit, this)}${SECOND_USER_END}"

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}