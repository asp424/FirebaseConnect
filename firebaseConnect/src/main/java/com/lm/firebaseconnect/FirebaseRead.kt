package com.lm.firebaseconnect

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
    fun readNode(node: Nodes, digit: String, key: String = digit, onRead: (String) -> Unit) =
        firebaseSave.databaseReference.child(digit).child(node.node()).child(key).get()
            .addOnCompleteListener {
                it.result?.also { resultNotNull ->
                    resultNotNull.key?.also { keyNotNull ->
                        if (keyNotNull == key) onRead(resultNotNull.value.toString())
                    }
                }
            }

    private fun startMessagesListener(onMessage: (List<Pair<String, String>>) -> Unit) =
        CoroutineScope(IO).launch {
            callbackFlow { with(valueListener) { eventListener() } }.collect {
                if (it is RemoteLoadStates.Success) {
                    onMessage(it.data.children.map { v -> v.value.toString().getMessage() })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    fun String.getMessage() =
        with(firebaseSave.crypto.cipherDecrypt(this)) {
            if (this != "error" && isNotEmpty())
                Pair(
                    with(firebaseSave.timeConverter) {
                        substringAfter(D_T_E).currentTimeZoneTime()
                    }, if (substringAfter(D_T_S).substringBefore(D_T_E) ==
                        firebaseSave.firebaseChat.myDigit
                    ) MY_COLOR else CHAT_ID_COLOR
                ) else Pair("", CHAT_ID_COLOR)
        }

    fun startListener() = with(firebaseSave) {
        messagesJob.cancel()
        save(ONE, Nodes.ONLINE)
        firebaseSave.save(ONE, Nodes.ONLINE, firebaseSave.firebaseChat.myDigit)
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
        const val F_U_S = "<f>"
        const val F_U_E = "<*f>"
        const val S_U_S = "<s>"
        const val S_U_E = "<*s>"
        const val D_T_S = "<N>"
        const val D_T_E = "</N>"
        const val MY_COLOR = "green"
        const val CHAT_ID_COLOR = "black"
    }
}