package com.lm.firebaseconnect

import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.listeners.ChildEventListenerInstance
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.getUserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseHandler(
    private val firebaseSave: FirebaseSave,
    private val childEventListenerInstance: ChildEventListenerInstance,
    private val firebaseRead: FirebaseRead
) {

    fun startMainListener() {
        CoroutineScope(IO).launch {
            with(firebaseSave) { save(ONE, Nodes.ONLINE, myDigit) }
            listener().collect { t ->
                listUsers.value = UIUsersStates.Success(t.filter())
            }
        }.apply { listJobs.add(this) }
    }

    fun stopMainListener() {
        listJobs.onEach { it.cancel() }; with(firebaseSave) { save(ZERO, Nodes.ONLINE, myDigit) }
    }

    private val listJobs by lazy { mutableListOf<Job>() }

    private suspend fun List<DataSnapshot>.filter() = withContext(IO) {
        with(firebaseSave) {
            filter { it.key != null }.filter {
                //it.key != firebaseConnect.myDigit &&
                it.key!!.isDigitsOnly()
            }.map {
                it.getUserModel(
                    it.key!!.getPairPath,
                    firebaseSave.firebaseChat.chatId, firebaseRead
                )
            }
        }
    }

    private fun listener() = callbackFlow {
        with(childEventListenerInstance) { childListener() }
    }
}