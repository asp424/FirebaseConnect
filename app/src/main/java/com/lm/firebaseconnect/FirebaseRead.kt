package com.lm.firebaseconnect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseRead(
    val firebaseSave: FirebaseSave
) {
    val onLineState = mutableStateOf(false)
    var writingState = mutableStateOf(false)
    var notifyState = mutableStateOf(false)
    val listMessages: MutableState<UIStates> = mutableStateOf(UIStates.Loading)

    private fun nodeListener(node: Nodes) = callbackFlow {
        firebaseSave.databaseReference.child(firebaseSave.himDigit).child(node.node()).apply {
            eventListener().also { listener ->
                addValueEventListener(listener)
                awaitClose { removeEventListener(listener) }
            }
        }
    }

    private fun messagesListener() = callbackFlow {
        firebaseSave.databaseReference.child(Nodes.CHATS.node())
            .child(firebaseSave.pairPath).apply {
            eventListener().also { listener ->
                addValueEventListener(listener)
                awaitClose { removeEventListener(listener) }
            }
        }
    }

    fun readToken() = callbackFlow {
        firebaseSave.databaseReference.child(firebaseSave.himDigit).child(Nodes.TOKEN.node()).apply {
            eventListener().also { listener ->
                addListenerForSingleValueEvent(listener)
                awaitClose { removeEventListener(listener) }
            }
        }
    }

    private fun ProducerScope<RemoteLoadStates>.eventListener() = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySendBlocking(RemoteLoadStates.Success(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            trySendBlocking(RemoteLoadStates.Failure(error))
        }
    }

    private fun startNodeListener(node: Nodes, onGet: CoroutineScope.(String) -> Unit) =
        CoroutineScope(IO).launch {
            nodeListener(node).collect { it.checkValue { v -> onGet(this, v) } }
        }

    private fun startMessagesListener(onMessage: (List<Pair<String, String>>) -> Unit) =
        CoroutineScope(IO).launch {
            messagesListener().collect {
                if (it is RemoteLoadStates.Success<*>) {
                    onMessage((it.data as DataSnapshot).children.toList().map { v ->
                        val message = firebaseSave.crypto.cipherDecrypt(v.value.toString())
                        val digit = message.getDigitFromMessage
                        val normMessage = with(firebaseSave.timeConverter){
                            message.removeDigitTags.currentTimeZoneTime()
                        }
                        if (digit == firebaseSave.myDigit) Pair(normMessage, "green")
                        else Pair(normMessage, "black")
                    })
                } else {
                    listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
                }
            }
        }

    private inline fun RemoteLoadStates.checkValue(crossinline onCheck: (String) -> Unit) =
        snapshot { ds -> if (ds.key == firebaseSave.pairPath) onCheck(ds.value.toString()) }

    inline fun RemoteLoadStates.snapshot(crossinline onGet: (DataSnapshot) -> Unit) {
        if (this is RemoteLoadStates.Success<*>) (data as DataSnapshot).children.map { onGet(it) }
    }

    inline fun startReadToken(crossinline onRead: (String) -> Unit) = CoroutineScope(IO).launch {
        readToken().collect { it.snapshot { w -> onRead(w.value.toString()) } }
    }

    fun stopListener() {
        messagesJob.cancel(); writingJob.cancel(); onlineJob.cancel(); notifyJob.cancel()
    }

    fun startListener() {
        stopListener()
        initStates()
        messagesJob = startMessagesListener { listMessages.value = UIStates.Success(it) }
        writingJob = startNodeListener(Nodes.WRITING) { writingState.value = (it != "0") }
        onlineJob = startNodeListener(Nodes.ONLINE) { onLineState.value = (it != "0") }
        notifyJob = startNodeListener(Nodes.NOTIFY) {
            if (it == RING) CoroutineScope(IO).launch {
                notifyState.value = true
                delay(3000)
                notifyState.value = false
                firebaseSave.clearHimNotify()
            }
        }
    }

    private fun initStates(){
        onLineState.value = false
        writingState.value = false
        listMessages.value = UIStates.Loading
    }

    private val String.getDigitFromMessage
        get() = substringAfter(DIGIT_TAG_START).substringBefore(DIGIT_TAG_END)

    private val String.removeDigitTags get() = substringAfter(DIGIT_TAG_END)

    private var messagesJob: Job = Job()

    private var writingJob: Job = Job()

    private var onlineJob: Job = Job()

    private var notifyJob: Job = Job()

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