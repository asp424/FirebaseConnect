package com.lm.firebaseconnectapp.data.one_tap_google

import android.content.IntentSender

sealed class OTGRegState {
    class OnSuccess(val intentSender: IntentSender): OTGRegState()
    class OnError(val message: String): OTGRegState()
}