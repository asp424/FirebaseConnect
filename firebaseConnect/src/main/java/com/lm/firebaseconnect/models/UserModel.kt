package com.lm.firebaseconnect.models

data class UserModel(
    val name: String = "",
    val token: String = "",
    val id: String = "",
    val onLine: Boolean = false,
    val isWriting: String = "",
    // val listMessages: List<String> = emptyList(),
    val iconUri: String = "",
    val lastMessage: String = "",
    // val listMessages: List<Pair<String, String>> = emptyList()
    val isFree: Boolean = true
)






