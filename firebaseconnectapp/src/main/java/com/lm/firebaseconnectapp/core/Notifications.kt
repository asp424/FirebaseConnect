package com.lm.firebaseconnectapp.core

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_CALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.R
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import kotlin.random.Random

class Notifications(
    private val notificationManager: NotificationManagerCompat,
    private val pendingIntentBuilder: (Int, Intent) -> PendingIntent,
    private val notificationBuilder: (String) -> NotificationCompat.Builder,
    private val intentBuilder: (String, String) -> Intent,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification() {
        createChannel()
        notificationManager.notify(id, notification)
    }

    private val notification get() = with(get) {
            notificationBuilder.invoke(typeMessage).apply {
                setAutoCancel(true)
                setContentText(textMessage)
                setContentTitle(title)
                setCategory(CATEGORY_CALL)
                setSmallIcon(R.drawable.ic_launcher_background)
                priority = PRIORITY_MAX
                if (INCOMING_CALL.isType) {
                    setDeleteIntent(pendingIntent(2, REJECT))
                    getAction("Отклонить", 3, REJECT)
                    getAction("Ответить", 4, ANSWER)
                }
            }.build()
        }

    private val id get() = if (MESSAGE.isType) Random(99).nextInt() else get.callingId.toInt()

    private fun NotificationCompat.Builder.getAction(text: String, code: Int, key: String) =
        addAction(1, text, pendingIntent(code, key))

    private fun pendingIntent(code: Int, key: String) =
        pendingIntentBuilder.invoke(code, intentBuilder.invoke(key, get.callingId))

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() = notificationManager.getNotificationChannel(get.typeMessage)
        ?: notificationManager.createNotificationChannel(channel())

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel() = NotificationChannel(get.typeMessage, get.typeMessage, IMPORTANCE_HIGH)
        .apply { setSound(null, null) }
}