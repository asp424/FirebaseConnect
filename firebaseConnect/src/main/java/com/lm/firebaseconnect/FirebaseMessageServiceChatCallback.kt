package com.lm.firebaseconnect

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.RING
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.WAIT

internal class FirebaseMessageServiceChatCallback() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(
        remoteMessageModel: RemoteMessageModel,
        activityManager: ActivityManager,
        packageName: String,
        notificationManager: NotificationManagerCompat,
        notificationBuilder: Builder
    ) = with(remoteMessageModel) {
        when (typeMessage) {
            MESSAGE -> {
                if (!isRun(activityManager, packageName)) {
                    showNotificationFromMessenger(
                        textMessage, name, notificationBuilder, notificationManager,
                        callingId, "0", "Message"
                    )
                    path(chatId, Nodes.NOTIFY).updateChildren(mapOf(chatPath to RING))
                }
            }
            INCOMING_CALL -> {
                if (!isRun(activityManager, packageName)) {
                    showNotificationFromMessenger(
                        "Вам звонок от $name", "Входящий вызов",
                        notificationBuilder, notificationManager,
                        callingId, "1", "IncomingCall"
                    )
                }
                path(callingId, Nodes.CALL)
                    .updateChildren(mapOf(callingId to GET_INCOMING_CALL))
                path(chatId, Nodes.CALL).updateChildren(mapOf(chatId to callingId))
            }

            REJECT -> {
                notificationManager.cancel(callingId.toInt())
                showNotificationFromMessenger(
                    "Вам звонил $name", "Пропущенный вызов",
                    notificationBuilder, notificationManager,
                    callingId, "2", "MissingCall"
                )
                path(chatId, Nodes.CALL).updateChildren(mapOf(chatId to WAIT))
            }
            RESET ->{
                path(chatId, Nodes.CALL).updateChildren(mapOf(chatId to WAIT))
            }
            else -> Unit
        }
        callState.value = remoteMessageModel
    }

    private fun path(child: String, node: Nodes) = databaseReference.child(child).child(node.node())

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
        message: String,
        name: String,
        notificationBuilder: Builder,
        notificationManager: NotificationManagerCompat,
        id: String,
        channelId: String,
        channelName: String
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        )
        notificationManager.notify(
            id.toInt(), notificationBuilder
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        )
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}