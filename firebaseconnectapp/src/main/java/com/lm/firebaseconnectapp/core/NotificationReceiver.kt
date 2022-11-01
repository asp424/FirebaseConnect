package com.lm.firebaseconnectapp.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.di.dagger.AppComponent
import com.lm.firebaseconnectapp.startJitsiMit

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.appComponent?.apply {
                when (intent?.action) {
                    ANSWER -> answerAction(context, intent)
                    REJECT -> rejectAction(intent)
            }
        }
    }

        private fun AppComponent.rejectAction(intent: Intent) {
            firebaseMessageServiceCallback().firebaseConnect.remoteMessages.reset(callState.value.token)
            cancelNotification(intent)
        }

        private fun AppComponent.answerAction(context: Context, intent: Intent) {
            firebaseMessageServiceCallback().firebaseConnect.remoteMessages.answer(callState.value.token)
            startJitsiMit(context, callState.value.token)
            cancelNotification(intent)
        }

        private fun AppComponent.cancelNotification(intent: Intent) = run {
            notificationManager().cancel(intent.getStringExtra("callingId")?.toInt() ?: 0)
            ringtone().stop()
        }
    }