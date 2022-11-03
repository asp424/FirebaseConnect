package com.lm.firebaseconnect

import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.listeners.ChildEventListenerInstance
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.getUserModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.callbackFlow

class FirebaseHandler(
    private val firebaseConnect: FirebaseConnect,
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
        filter { it.key != null }.filter {
            it.key != firebaseConnect.myDigit &&
            it.key!!.isDigitsOnly()
        }.map { it.getUserModel(it.key!!.pairPath, firebaseSave.firebaseChat.chatId, firebaseRead) }
    }

    private fun listener() = callbackFlow {
        with(childEventListenerInstance) { childListener() }
    }

    private val String.pairPath get() =
        "${FIRST_USER_START}${maxOf(firebaseConnect.myDigit, this)
        }${FIRST_USER_END}${SECOND_USER_START}${minOf(firebaseConnect.myDigit, this)
        }${SECOND_USER_END}"
}