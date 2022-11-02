package com.lm.firebaseconnectapp.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.getToken
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
        firebaseMessageServiceCallback()
            .firebaseConnect.remoteMessages.cancelCall(getToken, RESET)
        cancelNotification(intent)
    }

    private fun AppComponent.answerAction(context: Context, intent: Intent) {
        firebaseMessageServiceCallback()
            .firebaseConnect.remoteMessages.answer(getToken)
        startJitsiMit(context, getToken)
        cancelNotification(intent)
    }

    private fun AppComponent.cancelNotification(intent: Intent) = run {
        notificationManager().cancel(intent.getStringExtra(CALLING_ID)?.toInt() ?: 0)
        ringtone().stop()
    }
}