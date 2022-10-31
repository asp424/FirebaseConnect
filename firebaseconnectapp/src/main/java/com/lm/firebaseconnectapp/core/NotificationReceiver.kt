package com.lm.firebaseconnectapp.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lm.firebaseconnect.State
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnect.State.remoteMessageModel
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.startJitsiMit

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.appComponent?.apply {
            ringtone().stop()
            notificationManager().cancel(intent?.getStringExtra("callingId")?.toInt()?:0)
            if (intent?.action == "get"){
                firebaseMessageServiceCallback().firebaseConnect.answer(callState.value.token)
                callState.value = remoteMessageModel.busy
                startJitsiMit(context, "a")
            }
            if (intent?.action == "cancel"){
                firebaseMessageServiceCallback().firebaseConnect.reject(callState.value.token)
                callState.value = remoteMessageModel.resetCall
            }
        }
    }
}