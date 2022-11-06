package com.lm.firebaseconnect.models

import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseRead
import com.lm.firebaseconnect.States.onLineState
import com.lm.firebaseconnect.States.writingState

data class UserModel(
    val name: String = "",
    val token: String = "",
    val id: String = "",
    val onLine: Boolean = false,
    val isWriting: Boolean = false,
    val listMessages: List<String> = emptyList(),
    val iconUri: String = "",
    val lastMessage: String = "",
)

fun DataSnapshot.getUserModel(
    pairPath: String,
    chatId: String,
    firebaseRead: FirebaseRead
) = UserModel(
    id = key ?: "",
    name = getValue(key ?: "", Nodes.NAME),
    onLine = getValue(key ?: "", Nodes.ONLINE).apply {
        if (key == chatId) onLineState.value = (this == "1")
    } == "1",
    isWriting = getValue(pairPath, Nodes.WRITING).apply {
        if (key == chatId) writingState.value = (this == "1")
    } == "1",
    token = getValue(key ?: "", Nodes.TOKEN),
    lastMessage = with(firebaseRead) {
        getValue(pairPath, Nodes.LAST).getMessage().first.ifEmpty { "Сообщений пока нет" }
    },
    iconUri = getValue(key ?: "", Nodes.ICON)
)

fun DataSnapshot.getValue(path: String, node: Nodes) =
    child(node.node()).child(path).value?.run { toString() } ?: ""


