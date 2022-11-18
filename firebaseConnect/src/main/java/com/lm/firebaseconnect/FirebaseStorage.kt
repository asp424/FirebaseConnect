package com.lm.firebaseconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseStorage {

    @SuppressLint("SuspiciousIndentation")
    fun saveSound(context: Context, timestamp: String, onComplete: (String) -> Unit) {
        val file = File(
            ContextWrapper(context).getDir("sounds", MODE_PRIVATE), "$timestamp.mp3"
        )

        timestamp.child.putFile(Uri.fromFile(file)).addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete("")
                    file.delete()
                }
            }.addOnFailureListener { onComplete(it.message.toString()) }
    }

    @SuppressLint("SuspiciousIndentation")
    fun readSound(timestamp: String, onComplete: (String) -> Unit) {
        timestamp.child.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) onComplete(it.result.toString())
        }.addOnFailureListener { onComplete(it.message.toString()) }
    }

    private val String.path get() = "sounds/${this}.mp3"
    private val String.child get() = storage.child(path)

    private val storage by lazy { Firebase.storage.reference }
}