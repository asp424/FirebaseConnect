package com.lm.firebaseconnect

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.REJECT

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
internal class FBMessageService : FirebaseMessagingService() {

    private val notificationManager
            by lazy { NotificationManagerCompat.from(this) }

    private val activityManager
            by lazy { getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    private val notificationBuilder
            by lazy { NotificationCompat.Builder(this, resources.getString(R.string.id)) }

    private val firebaseMessageServiceChatCallback by lazy {
        FirebaseMessageServiceChatCallback()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessageModel.getFromRemoteMessage(remoteMessage).apply {
            when (typeMessage) {
                MESSAGE -> {
                    if (!isRun(activityManager, packageName)) {
                        showNotificationFromMessenger(
                            textMessage, name, notificationBuilder, notificationManager
                        )
                        firebaseMessageServiceChatCallback
                            .sendCallBack(chatPath, chatId, typeMessage, callingId)
                    }
                }
                INCOMING_CALL -> {
                    if (!isRun(activityManager, packageName)) {
                        showNotificationFromMessenger(
                            "Звонок", name, notificationBuilder, notificationManager
                        )
                    }
                    callState.value = this
                    firebaseMessageServiceChatCallback
                        .sendCallBack(chatPath, chatId, typeMessage, callingId)
                }
                REJECT -> {
                    firebaseMessageServiceChatCallback
                        .sendCallBack(chatPath, chatId, typeMessage, callingId)
                    callState.value = this
                }
                else -> callState.value = this
            }
        }
    }

    private fun isRun(activityManager: ActivityManager, packageName: String): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance ==
                ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND && i.processName == packageName
            ) return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationFromMessenger(
        message: String, name: String, notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel("1", "ass", NotificationManager.IMPORTANCE_DEFAULT)
        )
        notificationManager.notify(
            1, notificationBuilder
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        )
    }
}





