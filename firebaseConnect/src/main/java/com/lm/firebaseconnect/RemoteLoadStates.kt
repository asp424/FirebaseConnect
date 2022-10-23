package com.lm.firebaseconnect

internal sealed interface RemoteLoadStates {
    data class Success<T>(val data: T) : RemoteLoadStates
    data class Failure<T>(val data: T) : RemoteLoadStates
}