package com.lm.firebaseconnect.models

sealed interface UIMessagesStates {
    object Loading : UIMessagesStates
    class Success(val list: List<MessageModel>) : UIMessagesStates
}

