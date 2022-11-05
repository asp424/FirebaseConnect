package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_END
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_START
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START
import com.lm.firebaseconnect.models.Nodes

class FirebaseSave(
    val myDigit: String, val firebaseChat: FirebaseConnect, val timeConverter: TimeConverter,
    val myName: String, val crypto: Crypto
) {

    fun deleteAllMessages() { child.removeValue()
        with(crypto.cipherEncrypt("")
        ) {
            save(this, Nodes.LAST)
            save(this, Nodes.LAST, digit = firebaseChat.chatId)
        }
    }

    fun sendMessage(text: String, remoteMessages: RemoteMessages) =
        with(crypto.cipherEncrypt("${DIGIT_TAG_START}$myDigit${DIGIT_TAG_END}" +
                    "$myName(${timeConverter.currentTime}): $text")
        ){
        child.updateChildren(mapOf(databaseReference.push().key.toString() to this))
            save(this, Nodes.LAST)
            save(this, Nodes.LAST, digit = firebaseChat.chatId)
            remoteMessages.message(text)
    }

    private val child
        get() = databaseReference.child(Nodes.CHATS.node()).child(firebaseChat.chatId.getPairPath)

    fun save(
        value: String, node: Nodes, path: String
        = firebaseChat.chatId.getPairPath, digit: String = myDigit,
        onSave: () -> Unit = {}
    ) {
        databaseReference.child(digit).child(node.node()).updateChildren(mapOf(path to value))
            .addOnCompleteListener { onSave() }
    }

    val String.getPairPath
        get() = if (myDigit.isNotEmpty() && isNotEmpty())
            "${FIRST_USER_START}${maxOf(myDigit.filter { it != '0' }.toInt(), filter { it != '0' }.toInt())}${FIRST_USER_END}${
            SECOND_USER_START
        }${minOf(myDigit.filter { it != '0' }.toInt(), filter { it != '0' }. toInt())}${SECOND_USER_END}"
    else "0"

    val databaseReference by lazy {
        with(FirebaseDatabase.getInstance()) {
            reference
        }
    }
}
