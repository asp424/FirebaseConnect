package com.lm.firebaseconnect

import com.google.firebase.database.DatabaseError
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseRead(
    val firebaseSave: FirebaseSave,
    private val valueEventListenerInstance: ValueEventListenerInstance
) {

    private fun messagesListener() = callbackFlow {
        with(valueEventListenerInstance) { eventListener() }
    }

    fun readNode(node: Nodes, onRead: (String) -> Unit) = node.nodePath.get()
        .addOnCompleteListener {
            it.result?.also { resultNotNull ->
                resultNotNull.key?.also { keyNotNull ->
                    if (keyNotNull == getChatId) onRead(resultNotNull.value.toString())
                }
            }
        }

    private val Nodes.nodePath
        get() = firebaseSave.databaseReference.child(getChatId).child(node()).child(getChatId)

    private fun startMessagesListener(onMessage: (List<Pair<String, String>>) -> Unit) =
        CoroutineScope(IO).launch {
            messagesListener().collect {
                if (it is RemoteLoadStates.Success) {
                    onMessage(it.data.children.map { v ->
                        val message = firebaseSave.crypto.cipherDecrypt(v.value.toString())
                        val digit = message.getDigitFromMessage
                        val normMessage = with(firebaseSave.timeConverter) {
                            message.removeDigitTags.currentTimeZoneTime()
                        }
                        if (digit == firebaseSave.myDigit) Pair(normMessage, "green")
                        else Pair(normMessage, "black")
                    })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    private fun stopListener() = messagesJob.cancel()

    private fun startListener() {
        stopListener()
        firebaseSave.save(ONE, Nodes.ONLINE)
        firebaseSave.clearHimNotify()
        messagesJob = startMessagesListener { listMessages.value = UIMessagesStates.Success(it) }
    }

    fun initStates() {
        onLineState.value = false; writingState.value = false
        listMessages.value = UIMessagesStates.Loading
    }

    private val String.getDigitFromMessage
        get() = substringAfter(DIGIT_TAG_START).substringBefore(DIGIT_TAG_END)

    fun onResume() = startListener()

    fun onPause() {
        stopListener(); firebaseSave.save(ZERO, Nodes.ONLINE); firebaseSave.save(ZERO, Nodes.WRITING)
    }

    private val getChatId get() = firebaseSave.firebaseChat.chatId

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
    }
}