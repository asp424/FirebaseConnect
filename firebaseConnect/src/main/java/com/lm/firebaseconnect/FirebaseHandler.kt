package com.lm.firebaseconnect

import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_END
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_START
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.RING
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.callbackFlow

class FirebaseHandler(
    private val firebaseConnect: FirebaseConnect,
    val firebaseSave: FirebaseSave,
    private val childEventListenerInstance: ChildEventListenerInstance
) {

    fun startMainListener() {
        CoroutineScope(IO).launch {
            firebaseSave.init()
            listener().collect { t ->
                t.setUiState {
                    if (it.key == firebaseSave.firebaseChat.chatId &&
                        it.getValue(it.key!!.pairPath, Nodes.NOTIFY) == RING
                    ) {
                        notifyState.value = true; delay(3000)
                        notifyState.value = false; firebaseSave.clearHimNotify()
                    }
                }
                listUsers.value = UIUsersStates.Success(t.filter())
            }
        }.apply { listJobs.add(this) }
    }

    val String.getDigitFromMessage
        get() = substringAfter(DIGIT_TAG_START).substringBefore(DIGIT_TAG_END)

    val String.removeDigitTags get() = substringAfter(DIGIT_TAG_END)

    private suspend fun List<DataSnapshot>.setUiState(
        onEachUser: suspend CoroutineScope.(DataSnapshot) -> Unit
    ) = withContext(IO) { forEach { onEachUser(this, it) } }

    fun stopMainListener() {
        listJobs.onEach { it.cancel() }
        with(firebaseSave) { save(ZERO, Nodes.ONLINE, myDigit) }
    }

    private val listJobs by lazy { mutableListOf<Job>() }

    private suspend fun List<DataSnapshot>.filter() = withContext(IO) {
        filter { it.key != null }.filter {// it.key != firebaseConnect.myDigit &&
            it.key!!.isDigitsOnly()
        }.map { it.getUserModel(it.key!!.pairPath, firebaseSave.firebaseChat.chatId) }
    }

    private fun listener() = callbackFlow {
        with(childEventListenerInstance) { childListener() }
    }

    private val String.pairPath
        get() = "${FIRST_USER_START}${
            maxOf(firebaseConnect.myDigit, this)
        }${FIRST_USER_END}${SECOND_USER_START}${
            minOf(firebaseConnect.myDigit, this)
        }${SECOND_USER_END}"
}