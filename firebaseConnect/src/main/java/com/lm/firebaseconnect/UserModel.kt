package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class UserModel(
    var name: String = "",
    var token: String = "",
    var id: String = "",
    var onLine: String = "",
    var isWriting: String = "",
    var listMessages: List<String> = emptyList()
)

fun DataSnapshot.getUserModel(
    pairPath: String?, chatId: String,
    onNotify: () -> Unit
) = UserModel().apply {
    pairPath?.also { path ->
        id = key ?: ""
        name = "name"
        token = getValue(path, Nodes.TOKEN)
        onLine = getValue(path, Nodes.ONLINE).apply {
            if (key == chatId) onLineState.value = (this == "1")
        }
        isWriting = getValue(path, Nodes.WRITING).apply {
            if (key == chatId) writingState.value = (this == "1")
        }
        getValue(path, Nodes.NOTIFY).apply {
            if (key == chatId && this == FirebaseRead.RING)
                CoroutineScope(Dispatchers.IO).launch {
                    notifyState.value = true
                    delay(3000)
                    notifyState.value = false
                    onNotify()
                }
        }
    }
}

fun DataSnapshot.getValue(path: String, node: Nodes)
= child(node.node()).child(path).value.toString()

