package com.lm.firebaseconnect

import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseConnect.Companion.ZERO
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.States.writingState
import com.lm.firebaseconnect.TimeConverter.Companion.T_T_E
import com.lm.firebaseconnect.TimeConverter.Companion.T_T_S
import com.lm.firebaseconnect.listeners.ValueEventListenerInstance
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteLoadStates
import com.lm.firebaseconnect.models.TypeMessage
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

    private fun startMessagesListener(onDone: List<MessageModel>.() -> Unit) =
        CoroutineScope(IO).launch {
            callbackFlow { with(valueListener) { eventListener() } }.collect {
                if (it is RemoteLoadStates.Success) {
                    var prevDate = ""
                    onDone(it.data.children.map { v ->
                        val message = v.value.toString().decrypt()
                        val key = v.key.toString()
                        message.getMessageModel(key, prevDate).apply {
                            prevDate = with(firebaseSave.timeConverter) {
                                formatDate(message.parseTimestamp())
                            }
                        }
                    })
                } else listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
            }
        }

    private fun String.getMessageModel(key: String, prevDate: String) =
        with(firebaseSave.timeConverter) {
            val digit = parseDigit()
            val side = if (digit == firebaseSave.firebaseConnect.myDigit) MY_COLOR
            else CHAT_ID_COLOR
            val wasRead = if (side == MY_COLOR && !startsWith(NEW)) 1.dp else 0.dp
            val date = formatDate(parseTimestamp())
            val type = if (contains(IS_RECORD)) TypeMessage.VOICE else TypeMessage.MESSAGE
            val isNew = contains(NEW)
            val replyKey = getReplyKey()
            val isReply = replyKey.isNotEmpty()
            MessageModel(
                type = type,
                text = if (type == TypeMessage.MESSAGE) getText() else getVoiceText(),
                alignment = if (side == MY_COLOR) CenterEnd else CenterStart,
                key = key,
                time = getTimeToMessage(),
                timeStamp = parseTimestamp(),
                voiceTimeStamp = getVoiceTimestamp(),
                name = getName(),
                wasRead = wasRead,
                wasReadColor = if (side == MY_COLOR && wasRead == 1.dp) Green else Gray,
                digit = digit,
                mustSetWasRead = side != MY_COLOR && isNew,
                date = formatDate(parseTimestamp()),
                isNewDate = prevDate != date,
                topStartShape = if (side == MY_COLOR) 20.dp else 0.dp,
                bottomEndShape = if (side == MY_COLOR) 0.dp else 20.dp,
                isReply = isReply,
                replyKey = replyKey
            )
        }

    fun DataSnapshot.getUserModel(pairPath: String) = UserModel(
        id = key ?: "",
        name = getValue(key ?: "", Nodes.NAME),
        onLine = getValue(key ?: "", Nodes.ONLINE) == ONE,
        isWriting = getValue(pairPath, Nodes.WRITING),
        token = getValue(key ?: "", Nodes.TOKEN),
        lastMessage = getValue(pairPath, Nodes.LAST).decrypt().removeKey().ifEmpty { EMPTY },
        iconUri = getValue(key ?: "", Nodes.ICON),
    )

    fun DataSnapshot.getValue(path: String, node: Nodes) =
        child(node.node()).child(path).value?.run { toString() } ?: ""

    fun startListener() = with(firebaseSave) {
        messageJob.cancel()
        messageJob = startMessagesListener {
            find { it.mustSetWasRead }?.apply { isUnreadFlag = true }
            filter { it.isReply }.map { m ->
                find { m.replyKey == it.key }?.also { replied ->
                    m.replyName = replied.name
                    m.replyText = replied.text
                }
            }
            listMessages.value = UIMessagesStates.Success(this)
        }
    }

    fun initStates() {
        UIMessagesStates.Loading; onLineState.value = false; writingState.value = false
    }

    private fun String.decrypt() = if (this != ERROR && isNotEmpty())
        firebaseSave.crypto.cipherDecrypt(this)
    else "error"

    private fun String.parseDigit() = substringAfter(D_T_S).substringBefore(D_T_E)

    private fun String.removeKey() = substringAfter("):")

    private fun String.getReplyKey() = substringAfter(R_T_S).substringBefore(R_T_E)

    private fun String.getText() = substringAfter(T_T_E).substringBefore(R_T_S)

    private fun String.getVoiceTimestamp() = substringAfter(IS_RECORD).substringBefore(R_T_S)

    private fun String.getName() = substringAfter(D_T_E).substringBefore(T_T_S)

    private fun String.getVoiceText() = substringAfter(T_T_E).substringBefore(R_T_S)

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
        const val F_U_S = "<f>"
        const val F_U_E = "<*f>"
        const val S_U_S = "<s>"
        const val S_U_E = "<*s>"
        const val D_T_S = "<N>"
        const val D_T_E = "</N>"
        const val MY_COLOR = "green"
        const val CHAT_ID_COLOR = "black"
        const val IS_RECORD = "<*R>isRecord<*/R>"
        const val NEW = "<*New>"
        const val R_T_S = "<*RP>"
        const val R_T_E = "<*/RP>"
        const val EMPTY = "empty"
        const val ERROR = "error"
    }
}