package com.lm.firebaseconnect.models

import com.google.firebase.database.DataSnapshot
import com.lm.firebaseconnect.FirebaseRead

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
)






