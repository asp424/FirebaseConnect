package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.D_T_E
import com.lm.firebaseconnect.FirebaseRead.Companion.D_T_S
import com.lm.firebaseconnect.FirebaseRead.Companion.F_U_E
import com.lm.firebaseconnect.FirebaseRead.Companion.F_U_S
import com.lm.firebaseconnect.FirebaseRead.Companion.NEW
import com.lm.firebaseconnect.FirebaseRead.Companion.R_T_E
import com.lm.firebaseconnect.FirebaseRead.Companion.R_T_S
import com.lm.firebaseconnect.FirebaseRead.Companion.S_U_E
import com.lm.firebaseconnect.FirebaseRead.Companion.S_U_S
import com.lm.firebaseconnect.TimeConverter.Companion.T_T_E
import com.lm.firebaseconnect.TimeConverter.Companion.T_T_S
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.Nodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FirebaseSave(
    val firebaseConnect: FirebaseConnect, val timeConverter: TimeConverter, val crypto: Crypto
) {

    fun deleteAllMessages() {
        child.removeValue()
        with(
            crypto.cipherEncrypt("")
        ) {
            save(this, Nodes.LAST)
            save(this, Nodes.LAST, digit = firebaseConnect.chatId)
        }
    }

    fun sendMessage(
        text: String,
        remoteMessages: RemoteMessages,
        newFlag: String = NEW,
        timeStamp: String = timeConverter.currentTime.toString(),
        key: String = databaseReference.push().key.toString(),
        name: String = firebaseConnect.myName,
        digit: String = firebaseConnect.myDigit,
        replyKey: String = "",
        onSend: () -> Unit = {}
    ) = with(crypto.cipherEncrypt(
            "$newFlag${D_T_S}${digit}${D_T_E}${name}${T_T_S}$timeStamp${T_T_E}$text$R_T_S$replyKey$R_T_E"
        )
    ) {
        child.updateChildren(mapOf(key to this))
            .addOnCompleteListener {
                save(this, Nodes.LAST) {
                    save(this, Nodes.LAST, digit = firebaseConnect.chatId) {
                        if (newFlag.isNotEmpty()) remoteMessages.message(text)
                        onSend()
                    }
                }
            }
    }

    fun MessageModel.setWasRead()
    = sendMessage(text, firebaseConnect.remoteMessages,
        "", timeStamp, key, name, digit, replyKey)

    private val child
        get() = databaseReference.child(Nodes.CHATS.node())
            .child(firebaseConnect.chatId.getPairPath)

    fun save(
        value: String,
        node: Nodes,
        path: String = firebaseConnect.chatId.getPairPath,
        digit: String = firebaseConnect.myDigit,
        onSave: () -> Unit = {}
    ) {
        CoroutineScope(IO).launch {
            databaseReference.child(digit).child(node.node()).updateChildren(mapOf(path to value))
                .addOnCompleteListener { onSave() }
        }
    }

    val String.getPairPath
        get() = if (checkBothDigits) "${F_U_S}${maxOf(myRemoveZero, removeZero)}${F_U_E}${S_U_S}${
            minOf(myRemoveZero, removeZero)
        }${S_U_E}"
        else "000000"

    fun getPairPathFromRemoteMessage(myDigit: String, chatId: String) =
        if (checkDigits(myDigit, chatId)) "${F_U_S}${
            maxOf(myDigit.removeZero, chatId.removeZero)
        }${F_U_E}${S_U_S}${
            minOf(myDigit.removeZero, chatId.removeZero)
        }${S_U_E}"
        else "111111"

    private fun checkDigits(myDigit: String, chatId: String) =
        myDigit.any { it.isDigit() } && chatId.any { it.isDigit() }

    private val String.checkBothDigits
        get() = firebaseConnect.myDigit.any { it.isDigit() } && any { it.isDigit() }

    private val myRemoveZero get() = firebaseConnect.myDigit.removeZero

    private val String.removeZero
        get() = takeIf { it.isNotEmpty() }?.filter { it != '0' }?.toInt() ?: 1984

    val databaseReference by lazy { with(FirebaseDatabase.getInstance()) { reference } }
}
