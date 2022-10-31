package com.lm.firebaseconnectapp.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.R
import com.lm.firebaseconnect.State
import javax.inject.Named
import kotlin.random.Random

class Notifications(
    val notificationManager: NotificationManagerCompat,
    private val pendingIntentBuilder: (Int, Intent) -> PendingIntent,
    private val notificationBuilder: (String) -> NotificationCompat.Builder,
    private val intentBuilder: (String, String) -> Intent,
    @Named("Ringtone")
    private val ringtone: Ringtone,
) {

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotificationFromMessenger(channelId: String) = with(State.callState.value) {
        createChannel(channelId)
        val getCallIntent = intentBuilder.invoke("get", callingId)

        val cancelIntent = intentBuilder.invoke("cancel", callingId)

        notificationManager.notify(
            if (typeMessage == State.MESSAGE) Random(99).nextInt() else callingId.toInt(),
            notificationBuilder.invoke(channelId).apply {
                setAutoCancel(true)
                setContentText(textMessage)
                setContentTitle(name)
                if (typeMessage == State.INCOMING_CALL) {
                    setContentIntent(
                        pendingIntentBuilder.invoke(
                            1, intentBuilder.invoke("off", callingId)
                        )
                    )
                    setDeleteIntent(pendingIntentBuilder.invoke(4,
                        intentBuilder.invoke("delete", callingId)))
                    addAction(
                        1, "Отклонить",
                        pendingIntentBuilder.invoke(2, cancelIntent)
                    )
                    addAction(
                        1, "Ответить", pendingIntentBuilder
                            .invoke(
                                3, intentBuilder.invoke("get", callingId)
                            )
                    )
                }
                setCategory(NotificationCompat.CATEGORY_CALL)
                priority = NotificationCompat.PRIORITY_MAX
                setSmallIcon(R.drawable.ic_launcher_background)
            }.build()
        )
    }

    private fun createChannel(channelId: String) = with(State.callState.value) {
        val channel = NotificationChannel(
            channelId,
            typeMessage,
            NotificationManager.IMPORTANCE_HIGH
        ).apply { setSound(null, null) }
        notificationManager.getNotificationChannel(channelId)
            ?: notificationManager.createNotificationChannel(channel)
    }
}