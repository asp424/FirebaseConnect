package com.lm.firebaseconnectapp.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.getToken
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.startJitsiMit
import javax.inject.Inject
import javax.inject.Named

class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var firebaseConnect: FirebaseConnect

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @[Inject Named("Ringtone")]
    lateinit var ringtone: Ringtone

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply {
            appComponent.inject(this@NotificationReceiver)
            when (intent?.action) {
                ANSWER -> answerAction(context, intent)
                REJECT -> rejectAction(intent)
                MESSAGE -> messageAction(context, intent)
            }
        }
    }

    private fun rejectAction(intent: Intent) {
        firebaseConnect.remoteMessages.cancelCall(getToken, RESET)
        cancelNotification(intent)
    }

    private fun answerAction(context: Context, intent: Intent) {
        firebaseConnect.remoteMessages.answer(getToken)
        startJitsiMit(context, getToken)
        cancelNotification(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun messageAction(context: Context, intent: Intent) {
        intent.extras?.getString(CALLING_ID).log
        val newIntent = Intent(context, MainActivity::class.java).apply {
            action = MESSAGE
            flags = FLAG_ACTIVITY_NEW_TASK
            intent.extras?.let { putExtras(it) }
        }
        context.startActivity(newIntent)
    }

    private fun cancelNotification(intent: Intent) = run {
        notificationManager.cancel(intent.getStringExtra(CALLING_ID)?.toInt() ?: 0)
        ringtone.stop()
    }
}