package com.lm.firebaseconnectapp.data.one_tap_google

import android.net.Uri

sealed interface FBRegStates {
    class OnSuccess(val iconUri: Uri?, val name: String?): FBRegStates
    class OnError(val message: String): FBRegStates
    class OnClose(val message: String): FBRegStates
}