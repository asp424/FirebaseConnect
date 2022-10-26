package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_END
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_START
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import com.lm.firebaseconnect.State.BUSY
import com.lm.firebaseconnect.State.WAIT

class FirebaseSave(
    val myDigit: String,
    val firebaseChat: FirebaseConnect,
    val timeConverter: TimeConverter,
    val myName: String,
    val crypto: Crypto
) {

    fun deleteAllMessages() {
        Nodes.MESSAGES.node().child.removeValue()
    }

    fun sendMessage(text: String, remoteMessages: RemoteMessages) {
        Nodes.MESSAGES.node().child.updateChildren(
            mapOf(
                randomId to crypto.cipherEncrypt(
                    "$digitForMessage$myName(${timeConverter.currentTime}): $text"
                )
            )
        )
        remoteMessages.message(text)
    }

    private val String.child
        get() = databaseReference.child(Nodes.CHATS.node()).child(pairPath).child(this)

    fun clearHimNotify() = databaseReference.child(firebaseChat.chatId).child(Nodes.NOTIFY.node())
        .updateChildren(mapOf(pairPath to FirebaseRead.CLEAR_NOTIFY))

    private fun save(
        value: String,
        node: String,
        path: String = pairPath,
        digit: String = myDigit
    ) {
        databaseReference.child(digit).child(node).updateChildren(mapOf(path to value))
    }

    fun saveWait() = save(WAIT, Nodes.CALL.node())

    fun saveBusy() {
        save(BUSY, Nodes.CALL.node())
        save(BUSY, Nodes.CALL.node(), firebaseChat.chatId)
    }

    fun saveWriting(value: String) = save(value, Nodes.WRITING.node())

    fun saveOnline(value: String) = save(value, Nodes.ONLINE.node())

    fun saveOnlineApp(value: String) = save(value, Nodes.ONLINE.node(), myDigit)

    private val digitForMessage get() = "${DIGIT_TAG_START}$myDigit${DIGIT_TAG_END}"

    private val randomId get() = databaseReference.push().key.toString()

    val pairPath
        get() = "${FIRST_USER_START}${maxOf(myDigit, firebaseChat.chatId)}${FIRST_USER_END}${
            SECOND_USER_START
        }${minOf(myDigit, firebaseChat.chatId)}${SECOND_USER_END}"

    val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}
