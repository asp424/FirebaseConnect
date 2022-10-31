package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.State.listMessages
import com.lm.firebaseconnect.State.onLineState
import com.lm.firebaseconnect.State.writingState
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
    private val valueEventListenerInstance: ValueEventListenerInstance
) {

    private fun messagesListener() = callbackFlow {
        with(valueEventListenerInstance) { eventListener() }
    }

    fun readNode(node: Nodes, digit: String, onRead: (String) -> Unit)
    = nodePath(digit, node).get()
        .addOnCompleteListener {
            it.result?.also { resultNotNull ->
                resultNotNull.key?.also { keyNotNull ->
                    if (keyNotNull == digit) onRead(resultNotNull.value.toString())
                }
            }
        }

    private fun nodePath(digit: String, node: Nodes) =
        firebaseSave.databaseReference.child(digit).child(node.node()).child(digit)

    private fun startMessagesListener(onMessage: (List<Pair<String, String>>) -> Unit) =
        CoroutineScope(IO).launch {
            messagesListener().collect {
                if (it is RemoteLoadStates.Success) {
                    onMessage(it.data.children.map { v -> v.getMessage() })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    private fun DataSnapshot.getMessage() =
        with(firebaseSave.crypto.cipherDecrypt(value.toString())) {
            Pair(with(firebaseSave.timeConverter) {
                removeDigitTags.currentTimeZoneTime()
            }, if (getDigitFromMessage == firebaseSave.myDigit) MY_COLOR else CHAT_ID_COLOR)
        }

    private fun stopListener() = messagesJob.cancel()

    private fun startListener() = with(firebaseSave) {
        stopListener()
        save(ONE, Nodes.ONLINE)
        save(CLEAR_NOTIFY, Nodes.NOTIFY, digit = firebaseChat.chatId)
        messagesJob = startMessagesListener { listMessages.value = UIMessagesStates.Success(it) }
    }

    fun initStates() {
        onLineState.value = false
        writingState.value = false
        listMessages.value = UIMessagesStates.Loading
    }

    private val String.getDigitFromMessage
        get() = substringAfter(DIGIT_TAG_START).substringBefore(DIGIT_TAG_END)

    fun onResume() = startListener()

    fun onPause() {
        stopListener(); firebaseSave.save(ZERO, Nodes.ONLINE); firebaseSave.save(ZERO, Nodes.WRITING)
    }

    private val String.removeDigitTags get() = substringAfter(DIGIT_TAG_END)

    private var messagesJob: Job = Job()

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