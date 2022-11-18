package com.lm.firebaseconnect

import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.States.writingState
import com.lm.firebaseconnect.listeners.ValueEventListenerInstance
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteLoadStates
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnect.models.UserModel
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

    private fun startMessagesListener(onMessage: (List<MessageModel>) -> Unit) =
        CoroutineScope(IO).launch {
            callbackFlow { with(valueListener) { eventListener() } }.collect {
                if (it is RemoteLoadStates.Success) {
                    onMessage(it.data.children.map { v ->
                        with(v.value.toString().getMessage()) {
                            first.log
                            val type = if (first.removeKey().trimStart().startsWith(IS_RECORD))
                                2 else 1
                            with(firebaseSave.timeConverter) {
                                MessageModel(
                                    type = type,
                                    text = first.removeKey().trimStart(),
                                    alignment = if (second == "green") CenterEnd else CenterStart,
                                    key = first.parseKey(),
                                    time = first.getTime().currentTimeZoneTime(),
                                    timeStamp = first.substringAfter(IS_RECORD).parseTimestamp()
                                )
                            }
                        }
                    })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    private fun String.getMessage() =
        with(firebaseSave.crypto.cipherDecrypt(this)) {
            if (this != "error" && isNotEmpty())
                Pair(
                    substringAfter(D_T_E), if (parseDigit() == firebaseSave.firebaseConnect.myDigit
                    ) MY_COLOR else CHAT_ID_COLOR
                ) else Pair("", CHAT_ID_COLOR)
        }

    private fun String.parseDigit() = substringAfter(D_T_S).substringBefore(D_T_E)

    private fun String.getTime() = substringAfter("(").substringBefore("):")

    private fun String.parseKey() = substringAfter(M_K_S).substringBefore(M_K_E)

    private fun String.removeKey() = substringAfter("):")

    fun DataSnapshot.getUserModel(
        pairPath: String,
        firebaseRead: FirebaseRead,
        chatsSnapshot: DataSnapshot?
    ) = UserModel(
        id = key ?: "",
        name = getValue(key ?: "", Nodes.NAME),
        onLine = getValue(key ?: "", Nodes.ONLINE) == "1",
        isWriting = getValue(pairPath, Nodes.WRITING) == "1",
        token = getValue(key ?: "", Nodes.TOKEN),
        lastMessage = with(firebaseRead) {
            getValue(pairPath, Nodes.LAST)
                .getMessage().first.removeKey().ifEmpty { "Сообщений пока нет" }
        },
        iconUri = getValue(key ?: "", Nodes.ICON),
        // listMessages = with (firebaseRead) {
        //     chatsSnapshot?.child(pairPath)?.children?.map { it.value.toString().getMessage() }
        //         ?: emptyList()
        // }
    )

    fun DataSnapshot.getValue(path: String, node: Nodes) =
        child(node.node()).child(path).value?.run { toString() } ?: ""

    fun startListener() = with(firebaseSave) {
        messageJob.cancel()
        messageJob = startMessagesListener {
            listMessages.value = UIMessagesStates.Success(it)
        }
    }

    fun initStates() {
        UIMessagesStates.Loading
        onLineState.value = false
        writingState.value = false
    }

    private fun stopListener() {
        UIMessagesStates.Loading
    }

    private var messageJob: Job = Job()

    fun onPause() {
        stopListener()
        firebaseSave.save(ZERO, Nodes.ONLINE)
        firebaseSave.save(ZERO, Nodes.WRITING)
    }

    companion object {
        const val M_K_S = "<k>"
        const val M_K_E = "</k>"
        const val F_U_S = "<f>"
        const val F_U_E = "<*f>"
        const val S_U_S = "<s>"
        const val S_U_E = "<*s>"
        const val D_T_S = "<N>"
        const val D_T_E = "</N>"
        const val MY_COLOR = "green"
        const val CHAT_ID_COLOR = "black"
        const val IS_RECORD = "<*R>isRecord<*/R>"
    }
}