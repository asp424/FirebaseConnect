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

    fun getPairPathFromRemoteMessage(myDigit: String, chatId: String) = if (checkDigits(myDigit, chatId))
            "${F_U_S}${maxOf(myDigit.removeZero, chatId.removeZero)}${F_U_E}${S_U_S}${
                minOf(myDigit.removeZero, chatId.removeZero)
            }${S_U_E}"
        else "111111"

    private fun checkDigits(myDigit: String, chatId: String) =
        myDigit.any { it.isDigit() } && chatId.any { it.isDigit() }

    private val String.checkMyDigit
        get() = firebaseChat.myDigit.any { it.isDigit() } && any { it.isDigit() }

    private val myRemoveZero get() = firebaseChat.myDigit.removeZero

    private val String.removeZero get() = takeIf { it.isNotEmpty() }
        ?.filter { it != '0' }?.toInt()?: 1984

    val databaseReference by lazy { with(FirebaseDatabase.getInstance()) { reference } }
}
