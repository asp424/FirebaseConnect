package com.lm.firebaseconnectapp.notifications

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
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.getToken
import com.lm.firebaseconnect.models.Nodes
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
                REJECT -> rejectAction(intent)
                ANSWER -> answerAction(context, intent)
                MESSAGE -> messageAction(context, intent)
                INCOMING_CALL -> incomingCallAction(context)
            }
        }
    }

    private fun rejectAction(intent: Intent) {
        firebaseConnect.remoteMessages.cancelCall(RESET, getToken, get.destinationId, get.callingId)
        cancelNotification(intent)
    }

    private fun answerAction(context: Context, intent: Intent) {
        firebaseConnect.remoteMessages.callMessage(ANSWER, get.destinationId, get.token)
        firebaseConnect.firebaseRead.readNode(
            Nodes.NAME, get.destinationId, get.destinationId
        ) { name ->
            firebaseConnect.firebaseRead.readNode(
                Nodes.ICON, get.destinationId, get.destinationId
            ) { icon ->
                startJitsiMit(context, getToken, name, icon)
                cancelNotification(intent)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun messageAction(context: Context, intent: Intent) {
        val newIntent = Intent(context, MainActivity::class.java).apply {
            action = MESSAGE
            flags = FLAG_ACTIVITY_NEW_TASK
            intent.extras?.let { putExtras(it) }
        }
        context.startActivity(newIntent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun incomingCallAction(context: Context) {
        val newIntent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(newIntent)
    }

    private fun cancelNotification(intent: Intent) = run {
        notificationManager.cancel(intent.getStringExtra(CALLING_ID)?.toInt() ?: 0)
        ringtone.stop()
    }
}