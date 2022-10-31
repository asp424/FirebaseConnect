package com.lm.firebaseconnect.models

import com.google.firebase.database.DataSnapshot

sealed interface RemoteLoadStates {
    data class Success(val data: DataSnapshot) : RemoteLoadStates
    data class Failure<T>(val data: T) : RemoteLoadStates
}