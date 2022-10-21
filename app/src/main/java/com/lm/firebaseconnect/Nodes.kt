package com.lm.firebaseconnect

sealed interface Nodes{
    object MESSAGES: Nodes
    object CHATS: Nodes
    object ONLINE: Nodes
    object WRITING: Nodes
    object TOKEN: Nodes
    object NOTIFY: Nodes
    object CALL: Nodes
    object ANSWER: Nodes
    object REJECT: Nodes

    fun node() = when(this){
        MESSAGES -> "messages"
        ONLINE -> "online"
        WRITING -> "writing"
        TOKEN -> "token"
        NOTIFY -> "notify"
        CALL -> "call"
        ANSWER -> "answer"
        REJECT -> "reject"
        CHATS -> "chats"
    }
}