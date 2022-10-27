package com.lm.firebaseconnect

import com.google.firebase.database.DataSnapshot

data class UserModel(
    var name: String = "",
    var token: String = "",
    var id: String = "",
    var onLine: String = "",
    var isWriting: String = "",
    var listMessages: List<String> = emptyList()
)

fun DataSnapshot.getUserModel(pairPath: String, chatId: String) =
    UserModel().apply {
        id = key ?: ""
        name = "name"

        onLine = getValue(key ?: "", Nodes.ONLINE).apply {
            if (key == chatId) onLineState.value = (this == "1")
        }
        isWriting = getValue(pairPath, Nodes.WRITING).apply {
            if (key == chatId) writingState.value = (this == "1")
        }
        token = getValue(key ?: "", Nodes.TOKEN)
}

fun DataSnapshot.getValue(path: String, node: Nodes) =
    child(node.node()).child(path).value.toString()

