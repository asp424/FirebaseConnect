package com.lm.firebaseconnect.models

import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseRead
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.States.writingState

data class UserModel(
    var name: String = "",
    var token: String = "",
    var id: String = "",
    var onLine: Boolean = false,
    var isWriting: Boolean = false,
    var listMessages: List<String> = emptyList(),
    var iconUri: String = "",
    var lastMessage: String = "last message"
)

fun DataSnapshot.getUserModel(
    pairPath: String,
    chatId: String,
    firebaseRead: FirebaseRead
) = UserModel(
    id = key ?: "",
    name = "name",
    onLine = getValue(key ?: "", Nodes.ONLINE).apply {
        if (key == chatId) onLineState.value = (this == "1")
    } == "1",
    isWriting = getValue(pairPath, Nodes.WRITING).apply {
        if (key == chatId) writingState.value = (this == "1")
    } == "1",
    token = getValue(key ?: "", Nodes.TOKEN),
    lastMessage = with(firebaseRead){ getValue(pairPath, Nodes.LAST).getMessage().first }
)


fun DataSnapshot.getValue(path: String, node: Nodes) =
    child(node.node()).child(path).value.toString()


