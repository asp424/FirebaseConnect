package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.D_T_E
import com.lm.firebaseconnect.FirebaseRead.Companion.D_T_S
import com.lm.firebaseconnect.FirebaseRead.Companion.F_U_E
import com.lm.firebaseconnect.FirebaseRead.Companion.F_U_S
import com.lm.firebaseconnect.FirebaseRead.Companion.S_U_E
import com.lm.firebaseconnect.FirebaseRead.Companion.S_U_S
import com.lm.firebaseconnect.models.Nodes

class FirebaseSave(
    val firebaseChat: FirebaseConnect, val timeConverter: TimeConverter,
    val crypto: Crypto
) {

    fun deleteAllMessages() {
        child.removeValue()
        with(
            crypto.cipherEncrypt("")
        ) {
            save(this, Nodes.LAST)
            save(this, Nodes.LAST, digit = firebaseChat.chatId)
        }
    }

    fun sendMessage(text: String, remoteMessages: RemoteMessages) =
        with(
            crypto.cipherEncrypt(
                "${D_T_S}${firebaseChat.myDigit}${D_T_E}" +
                        "${firebaseChat.myName}(${timeConverter.currentTime}): $text"
            )
        ) {
            child.updateChildren(mapOf(databaseReference.push().key.toString() to this))
            save(this, Nodes.LAST)
            save(this, Nodes.LAST, digit = firebaseChat.chatId)
            remoteMessages.message(text)
        }

    private val child
        get() = databaseReference.child(Nodes.CHATS.node()).child(firebaseChat.chatId.getPairPath)

    fun save(
        value: String, node: Nodes, path: String
        = firebaseChat.chatId.getPairPath, digit: String = firebaseChat.myDigit,
        onSave: () -> Unit = {}
    ) {
        databaseReference.child(digit).child(node.node()).updateChildren(mapOf(path to value))
            .addOnCompleteListener { onSave() }
    }

    val String.getPairPath
        get() = if (checkMyDigit)
            "${F_U_S}${maxOf(myRemoveZero, removeZero)}${F_U_E}${S_U_S}${
                minOf(myRemoveZero, removeZero)
            }${S_U_E}"
        else "000000"


    private val String.checkMyDigit
        get() = firebaseChat.myDigit.any { it.isDigit() } && any { it.isDigit() }

    private val myRemoveZero get() = firebaseChat.myDigit.removeZero

    private val String.removeZero get() = filter { it != '0' }.toInt()

    val databaseReference by lazy {
        with(FirebaseDatabase.getInstance()) {
            reference
        }
    }
}
