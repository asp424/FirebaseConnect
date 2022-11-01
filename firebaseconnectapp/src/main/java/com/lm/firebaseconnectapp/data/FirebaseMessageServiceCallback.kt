package com.lm.firebaseconnectapp.data

import android.app.ActivityManager
import android.content.Context
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.CHECK_FOR_CALL
import com.lm.firebaseconnect.State.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnect.State.remoteMessageModel
import com.lm.firebaseconnectapp.core.Notifications
import com.lm.firebaseconnectapp.startJitsiMit
import javax.inject.Named


class FirebaseMessageServiceCallback(
    private val context: Context,
    val firebaseConnect: FirebaseConnect,
    private val activityManager: ActivityManager,
    private val notifications: Notifications,
    @Named("Ringtone") private val ringtone: Ringtone,
    @Named("Notify") private val notificationSound: Ringtone
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(remoteMessage: RemoteMessage) =
        remoteMessageModel.getFromRemoteMessage(remoteMessage).also { model ->
            if (model.typeMessage == GET_INCOMING_CALL) {
                if (callState.value.typeMessage == OUTGOING_CALL) callState.value = model
            } else {
                if (model.typeMessage != CHECK_FOR_CALL) {
                    if (model.typeMessage != MESSAGE) callState.value = model
                }
            }
            with(notifications) {
                with(firebaseConnect.remoteMessages) {
                    when (model.typeMessage) {
                        MESSAGE -> if (!isRun()) {
                            showNotification()
                            notificationSound.play()
                        }
                        CHECK_FOR_CALL -> checkForCall(model)
                        REJECT -> {
                            notificationManager.cancel(callState.value.callingId.toInt())
                            showNotification()
                            ringtone.stop()
                            notificationSound.play()
                            callState.value = remoteMessageModel.rejectCall
                        }

                        ANSWER -> startJitsiMit(context, model.token)
                        RESET -> callState.value = remoteMessageModel.rejectCall
                        GET_CHECK_FOR_CALL -> callCallBack(model)
                        INCOMING_CALL -> {
                            if (!isRun()) showNotification()
                            ringtone.play()
                        }
                        else -> Unit
                    }
                }
            }
        }

    private fun isRun(): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && i.processName == context.packageName) return true
        }
        return false
    }
/*
    private val sharedPreferences by lazy {
        context.getSharedPreferences("checkForFirst", MODE_PRIVATE)
    }

    private fun SharedPreferences.save(value: String) = edit().putString("callState", value).apply()

    private fun SharedPreferences.read() = getString("callState", "")

 */
}