package com.lm.firebaseconnectapp.data

import android.app.ActivityManager
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.State
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.core.Notifications
import javax.inject.Named


class FirebaseMessageServiceCallback(
    private val context: Context,
    val firebaseConnect: FirebaseConnect,
    private val activityManager: ActivityManager,
    private val notifications: Notifications,
    @Named("Ringtone")
    private val ringtone: Ringtone,
    @Named("Notify")
    private val notificationSound: Ringtone
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(remoteMessage: RemoteMessage) =
        with(State.remoteMessageModel.getFromRemoteMessage(remoteMessage)) {
            if (typeMessage == GET_INCOMING_CALL) {
                if (callState.value.typeMessage == State.OUTGOING_CALL) callState.value = this
            } else callState.value = this
            when (typeMessage) {
                MESSAGE -> {
                    if (!isRun()) {
                        notifications.showNotificationFromMessenger("0")
                        notificationSound.play()
                    }
                    firebaseConnect.setRingNotification()
                }
                INCOMING_CALL -> {
                    if (!isRun()) {
                        notifications.showNotificationFromMessenger("1")
                    }
                    firebaseConnect.sendRemoteMessage(GET_INCOMING_CALL)
                    ringtone.play()
                }

                REJECT -> {
                    notifications.notificationManager.cancel(callState.value.callingId.toInt())
                    if (!isRun()) {
                        notifications.showNotificationFromMessenger("2")
                        notificationSound.play()
                    }
                    ringtone.stop()
                }

                ANSWER -> {
                    firebaseConnect.answer(token)
                    callState.value = State.remoteMessageModel.busy
                }
                else -> Unit
            }
        }

    private fun isRun(): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance ==
                ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND && i.processName == context.packageName
            ) return true
        }
        return false
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences("checkForFirst", MODE_PRIVATE)
    }

    private fun SharedPreferences.save(value: String) = edit().putString("callState", value).apply()

    private fun SharedPreferences.read() = getString("callState", "")
}