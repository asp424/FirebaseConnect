package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseConnect.Companion.ONE
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_END
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_START
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.models.Nodes

class FirebaseSave(
    val myDigit: String, val firebaseChat: FirebaseConnect, val timeConverter: TimeConverter,
    val myName: String, val crypto: Crypto
) {

    fun deleteAllMessages() { Nodes.MESSAGES.node().child.removeValue() }

    fun sendMessage(text: String, remoteMessages: RemoteMessages) {
        Nodes.MESSAGES.node().child.updateChildren(
            mapOf(
                databaseReference.push().key.toString() to crypto.cipherEncrypt(
                    "${DIGIT_TAG_START}$myDigit${DIGIT_TAG_END}" +
                            "$myName(${timeConverter.currentTime}): $text"
                )
            )
        )
        remoteMessages.message(text)
    }

    private val String.child
        get() = databaseReference.child(Nodes.CHATS.node()).child(pairPath).child(this)

    fun save(
        value: String, node: Nodes, path: String = pairPath, digit: String = myDigit,
        onSave: () -> Unit = {}
    ) {
        databaseReference.child(digit).child(node.node()).updateChildren(mapOf(path to value))
            .addOnCompleteListener { onSave() }
    }

    fun init() {
        databaseReference.child(myDigit).get().addOnCompleteListener {
            if (!it.result.hasChild(Nodes.CALL.node())) save(WAIT, Nodes.CALL, myDigit)
            save(ONE, Nodes.ONLINE, myDigit)
        }
    }

    val pairPath
        get() = "${FIRST_USER_START}${maxOf(myDigit, firebaseChat.chatId)}${FIRST_USER_END}${
            SECOND_USER_START
        }${minOf(myDigit, firebaseChat.chatId)}${SECOND_USER_END}"

    val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}
