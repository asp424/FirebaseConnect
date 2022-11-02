package com.lm.firebaseconnectapp.data

import android.content.Context
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnectapp.core.Notifications
import com.lm.firebaseconnectapp.startJitsiMit
import javax.inject.Named

class FirebaseMessageServiceCallback(
    private val context: Context,
    val firebaseConnect: FirebaseConnect,
    private val notifications: Notifications,
    private val notificationManager: NotificationManagerCompat,
    @Named("Ringtone") private val ringtone: Ringtone,
    @Named("Notify") private val notificationSound: Ringtone,
    private val appIsInForeground: () -> Boolean
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(remoteMessage: RemoteMessage) = with(remoteMessageModel) {
        getFromRemoteMessage(remoteMessage).also { model ->
            with(notifications) {
                with(firebaseConnect.remoteMessages) {
                    when (model.typeMessage) {

                        MESSAGE -> if (!appIsInForeground()) {
                            model.set
                            showNotification()
                            notificationSound.play()
                            rejectCall.set
                        }

                        CHECK_FOR_CALL -> checkForCall(model)

                        REJECT -> {
                            model.set
                                notificationManager.cancel(get.callingId.toInt())
                            if (!appIsInForeground()) {
                                showNotification()
                                notificationSound.play()
                            }
                            ringtone.stop()
                            rejectCall.set
                        }

                        ANSWER -> startJitsiMit(context, model.token)

                        RESET -> rejectCall.set

                        GET_CHECK_FOR_CALL -> doCall(model)

                        INCOMING_CALL -> {
                            model.set
                            if (!appIsInForeground()) showNotification()
                            ringtone.play()
                        }

                        GET_INCOMING_CALL -> if (OUTGOING_CALL.isType) model.set
                    }
                }
            }
        }
    }

/*
    private val sharedPreferences by lazy {
        context.getSharedPreferences("checkForFirst", MODE_PRIVATE)
    }

    private fun SharedPreferences.save(value: String) = edit().putString("callState", value).apply()

    private fun SharedPreferences.read() = getString("callState", "")

 */
}