package com.lm.firebaseconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseStorage {

    @SuppressLint("SuspiciousIndentation")
    fun saveSound(context: Context, timestamp: String, onComplete: (String) -> Unit) {
        val file = File(
            ContextWrapper(context).getDir("sounds", Context.MODE_PRIVATE),
            "$timestamp.mp3"
        )

        timestamp.child.putFile(Uri.fromFile(file)).addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete("")
                    file.delete()
                }
            }.addOnFailureListener { onComplete(it.message.toString()) }
    }

    @SuppressLint("SuspiciousIndentation")
    fun readSound(timestamp: String, onComplete: (ByteArray) -> Unit) {
        storage.child("sounds/$timestamp.mp3")
            .getBytes(1024 * 1024)
            .addOnSuccessListener { onComplete(it) }
            .addOnFailureListener { onComplete(ByteArray(0)) }
    }

    private val String.child get() = storage.child("sounds/${this}.mp3")

    private val storage by lazy { Firebase.storage.reference }
}