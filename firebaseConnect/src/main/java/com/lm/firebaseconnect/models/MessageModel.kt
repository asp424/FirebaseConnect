package com.lm.firebaseconnect.models

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MessageModel(
    val key: String = "",
    val text: String = "",
    val alignment: Alignment = Alignment.CenterEnd,
    val type: TypeMessage = TypeMessage.MESSAGE,
    val time: String = "",
    val date: String = "",
    val timeStamp: String = "",
    val voiceTimeStamp: String = "",
    val name: String = "",
    val wasRead: Dp = 0.dp,
    val wasReadColor: Color = Color.Gray,
    val digit: String = "",
    val mustSetWasRead: Boolean = false,
    val isNewDate: Boolean = false,
    var isUnreadFlag: Boolean = false,
    var topStartShape: Dp = 0.dp,
    var bottomEndShape: Dp = 0.dp,
    var isReply: Boolean = false,
    var replyName: String = "",
    var replyText: String = "",
    var replyKey: String = "",
)
