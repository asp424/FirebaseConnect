package com.lm.firebaseconnectapp.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.R
import com.lm.firebaseconnect.State
import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.REJECT
import kotlin.random.Random

class Notifications(
    val notificationManager: NotificationManagerCompat,
    private val pendingIntentBuilder: (Int, Intent) -> PendingIntent,
    private val notificationBuilder: (String) -> NotificationCompat.Builder,
    private val intentBuilder: (String, String) -> Intent,
) {

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification() = with(State.callState.value) {
        createChannel()
        notificationManager.notify(
            if (typeMessage == State.MESSAGE) Random(99).nextInt() else callingId.toInt(),
            notificationBuilder.invoke(typeMessage).apply {
                setAutoCancel(true)
                setContentText(textMessage)
                setContentTitle(name)
                val cancelIntent = intentBuilder.invoke(REJECT, callingId)
                if (typeMessage == State.INCOMING_CALL) {
                    setDeleteIntent(pendingIntentBuilder.invoke(4, cancelIntent))
                    addAction(1, "Отклонить", pendingIntentBuilder.invoke(2, cancelIntent))
                    addAction(
                        1, "Ответить", pendingIntentBuilder
                            .invoke(
                                3, intentBuilder.invoke(ANSWER, callingId)
                            )
                    )
                }
                setCategory(NotificationCompat.CATEGORY_CALL)
                priority = NotificationCompat.PRIORITY_MAX
                setSmallIcon(R.drawable.ic_launcher_background)
            }.build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() = with(State.callState.value) {
        val channel = NotificationChannel(
            typeMessage,
            typeMessage,
            NotificationManager.IMPORTANCE_HIGH
        ).apply { setSound(null, null) }
        notificationManager.getNotificationChannel(typeMessage)
            ?: notificationManager.createNotificationChannel(channel)
    }
}