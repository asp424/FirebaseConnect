package com.lm.firebaseconnectapp.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_CALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import com.lm.firebaseconnect.R
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.presentation.IntentActivity
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import javax.inject.Named
import kotlin.random.Random

class Notifications(
    private val notificationManager: NotificationManagerCompat,
    @Named("Broadcast")
    private val pendingIntentBroadcastBuilder: (Int, Intent) -> PendingIntent,
    private val notificationBuilder: (String) -> NotificationCompat.Builder,
    private val intentBuilder: (String, Bundle) -> Intent,
    private val context: Application
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification() {
        createChannel()
        notificationManager.notify(id, notification)
    }

    private val notification
        get() = with(get) {
            notificationBuilder.invoke(typeMessage).apply {
                setAutoCancel(true)
                setContentText(text)
                setContentTitle(titles)
                setCategory(CATEGORY_CALL)
                setSmallIcon(R.drawable.ic_launcher_background)
                priority = PRIORITY_MAX
                if (INCOMING_CALL.isType) {
                    setDeleteIntent(pendingBroadcastIntent(2, REJECT))
                    getAction("Отклонить", 3, REJECT)
                    getAction("Ответить", 4, ANSWER)
                    setContentIntent(INCOMING_CALL.activityPendingIntent())
                }
                if (MESSAGE.isType)
                    setContentIntent(MESSAGE.activityPendingIntent())
                if (REJECT.isType)
                    setContentIntent(INCOMING_CALL.activityPendingIntent())
            }.build()
        }

    private fun String.activityPendingIntent() = with(get) {
            PendingIntent.getActivity(
                context, Random(99).nextInt(),
                Intent(
                    context, IntentActivity::class.java
                ).apply {
                    action = this@activityPendingIntent
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(NAME, name)
                    putExtra(CALLING_ID, callingId)
                    putExtra(TOKEN, token)
                    putExtra(ICON, icon)

                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

    private val id get() = if (MESSAGE.isType) Random(99).nextInt() else get.callingId.toInt()

    private val titles
        get() = when (get.typeMessage) {
            MESSAGE -> {
                if (get.textMessage.startsWith(IS_RECORD.trim()))
                "Голосовое сообщение от ${get.name}" else "Входящее сообщение от ${get.name}"
            }
            REJECT -> "Пропущенный вызов"
            INCOMING_CALL -> "Входящий вызов"
            else -> ""
        }.apply {
            get.textMessage.log
        }

    private val text
        get() = when (get.typeMessage) {
            MESSAGE -> {
                if (get.textMessage.startsWith(IS_RECORD.trim()))
                    "Голосовое сообщение" else "Сообщение: ${get.textMessage}"
            }
            REJECT -> "Вам звонил ${get.name}"
            INCOMING_CALL -> "Входящий вызов от ${get.name}"
            else -> ""
        }

    private fun NotificationCompat.Builder.getAction(text: String, code: Int, action: String) =
        addAction(1, text, pendingBroadcastIntent(code, action))

    private fun pendingIntent(code: Int, action: String) = with(get) {
        val bundle = Bundle().apply {
            putString(NAME, name)
            putString(CALLING_ID, callingId)
            putString(TOKEN, token)
            putString(ICON, icon)
        }
        pendingIntentBroadcastBuilder.invoke(code, intentBuilder.invoke(action, bundle))
    }

    private fun pendingBroadcastIntent(code: Int, action: String) = with(get) {
        val bundle = Bundle().apply { putString(CALLING_ID, callingId) }
        pendingIntentBroadcastBuilder.invoke(code, intentBuilder.invoke(action, bundle))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() = notificationManager.getNotificationChannel(get.typeMessage)
        ?: notificationManager.createNotificationChannel(channel())

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel() = NotificationChannel(get.typeMessage, get.typeMessage, IMPORTANCE_HIGH)
        .apply { setSound(null, null) }
}