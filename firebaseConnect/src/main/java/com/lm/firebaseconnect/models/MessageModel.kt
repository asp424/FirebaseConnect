package com.lm.firebaseconnect.models

import androidx.compose.ui.Alignment

data class MessageModel(
    val key: String = "",
    val text: String = "",
    val alignment: Alignment = Alignment.CenterEnd,
    val type: Int = 0,
    val time: String = "",
    val timeStamp: String = ""
)
