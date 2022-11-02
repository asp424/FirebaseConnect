package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.States.writingState
import com.lm.firebaseconnect.listeners.ValueEventListenerInstance
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteLoadStates
import com.lm.firebaseconnect.models.UIMessagesStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseRead(
    val firebaseSave: FirebaseSave,
    private val valueListener: ValueEventListenerInstance
) {
    fun readNode(node: Nodes, digit: String, onRead: (String) -> Unit)
    = firebaseSave.databaseReference.child(digit).child(node.node()).child(digit).get()
        .addOnCompleteListener {
            it.result?.also { resultNotNull ->
                resultNotNull.key?.also { keyNotNull ->
                    if (keyNotNull == digit) onRead(resultNotNull.value.toString())
                }
            }
        }

    private fun startMessagesListener(onMessage: (List<Pair<String, String>>) -> Unit) =
        CoroutineScope(IO).launch {
            callbackFlow { with(valueListener) { eventListener() } }.collect {
                if (it is RemoteLoadStates.Success) {
                    onMessage(it.data.children.map { v -> v.getMessage() })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    private fun DataSnapshot.getMessage() =
        with(firebaseSave.crypto.cipherDecrypt(value.toString())) {
            Pair(with(firebaseSave.timeConverter) {
                substringAfter(DIGIT_TAG_END).currentTimeZoneTime()
            }, if (substringAfter(DIGIT_TAG_START).substringBefore(DIGIT_TAG_END) ==
                firebaseSave.myDigit) MY_COLOR else CHAT_ID_COLOR)
        }

    fun startListener() = with(firebaseSave) {
        messagesJob.cancel()
        save(ONE, Nodes.ONLINE)
        save(CLEAR_NOTIFY, Nodes.NOTIFY, digit = firebaseChat.chatId)
        messagesJob = startMessagesListener { listMessages.value = UIMessagesStates.Success(it) }
    }

    fun initStates() {
        onLineState.value = false
        writingState.value = false
        listMessages.value = UIMessagesStates.Loading
    }

    fun onPause() {
        messagesJob.cancel()
        firebaseSave.save(ZERO, Nodes.ONLINE)
        firebaseSave.save(ZERO, Nodes.WRITING)
    }

    var messagesJob: Job = Job()

    companion object {
        const val FIRST_USER_START = "<f>"
        const val FIRST_USER_END = "<*f>"
        const val SECOND_USER_START = "<s>"
        const val SECOND_USER_END = "<*s>"
        const val DIGIT_TAG_START = "<N>"
        const val DIGIT_TAG_END = "</N>"
        const val RING = "RING"
        const val CLEAR_NOTIFY = "none"
        const val MY_COLOR = "green"
        const val CHAT_ID_COLOR = "black"
    }
}