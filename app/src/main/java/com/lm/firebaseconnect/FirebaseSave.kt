package com.lm.firebaseconnect
import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_END
import com.lm.firebaseconnect.FirebaseRead.Companion.DIGIT_TAG_START
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.FIRST_USER_START
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_END
import com.lm.firebaseconnect.FirebaseRead.Companion.SECOND_USER_START

class FirebaseSave(
    val myDigit: String,
    val himDigit: String,
    val timeConverter: TimeConverter,
    val myName: String,
    val crypto: Crypto
) {

    fun deleteAllMessages() = Nodes.MESSAGES.node().child.removeValue()

    fun sendMessage(text: String, fcmProvider: FCMProvider) {
        Nodes.MESSAGES.node().child.updateChildren(
            mapOf(
                randomId to crypto.cipherEncrypt(
                    "$digitForMessage$myName(${timeConverter.currentTime}): $text"
                )
            )
        )
        fcmProvider.sendChatRemoteMessage(text)
    }

    private val String.child
        get() = databaseReference.child(Nodes.CHATS.node()).child(pairPath).child(this)

    fun clearHimNotify() = databaseReference.child(himDigit).child(Nodes.NOTIFY.node())
        .updateChildren(mapOf(pairPath to FirebaseRead.CLEAR_NOTIFY))

    private fun save(value: String, node: String) =
        databaseReference.child(myDigit).child(node).updateChildren(mapOf(pairPath to value))

    fun saveWriting(value: String) = save(value, Nodes.WRITING.node())

    fun saveOnline(value: String) = save(value, Nodes.ONLINE.node())

    private val digitForMessage get() = "${DIGIT_TAG_START}$myDigit${DIGIT_TAG_END}"

    private val randomId get() = databaseReference.push().key.toString()

    val pairPath
        get() = "${FIRST_USER_START}${maxOf(myDigit, himDigit)}${FIRST_USER_END}${
            SECOND_USER_START
        }${minOf(myDigit, himDigit)}${SECOND_USER_END}"

    val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}