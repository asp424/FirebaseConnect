package com.lm.firebaseconnectapp.notifications

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_CALL
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.models.RemoteMessageModel
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.presentation.IntentActivity
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.showToast
import java.util.Calendar
import javax.inject.Named
import kotlin.random.Random

class Notifications(
    private val notificationManager: NotificationManagerCompat,
    @Named("Broadcast")
    private val pendingIntentBroadcastBuilder: (Int, Intent) -> PendingIntent,
    private val notificationBuilder: (String) -> NotificationCompat.Builder,
    private val intentBuilder: (String, Bundle) -> Intent,
    private val context: Application,
) {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(model: RemoteMessageModel, onShow: () -> Unit = {}, onFail: () -> Unit = {}) {
        createChannel(model)
        loadIcon(model.icon, onLoad = {
            notificationManager.notify(model.id, it.notification(model))
            onShow()
        }) {
            context.showToast("ошибка")
            onFail()
        }
    }

    private fun Bitmap.notification(model: RemoteMessageModel) = with(model) {
        val person = Person.Builder()
            .setIcon(IconCompat.createWithBitmap(this@notification))
            .setName(name)
            .setKey(textMessage)
            .build()
        notificationBuilder.invoke(typeMessage).apply {
            setAutoCancel(true)
            when (typeMessage) {
                MESSAGE -> {
                    setCategory(CATEGORY_MESSAGE)
                    setStyle(
                        NotificationCompat.MessagingStyle(person)
                            .addMessage(
                                NotificationCompat.MessagingStyle.Message(
                                    if (textMessage.contains(IS_RECORD))
                                        "Голосовое сособщение" else textMessage,
                                    currentTimestamp, person
                                )
                            ).setConversationTitle(name)
                    )
                    setContentTitle(titles)
                    setSmallIcon(R.drawable.baseline_message_24)
                    setContentIntent(MESSAGE.activityPendingIntent(model))
                }

                INCOMING_CALL -> {
                    setCategory(CATEGORY_CALL)
                    setLargeIcon(this@notification)
                    setContentText(name)
                    setContentTitle(titles)
                    setSmallIcon(R.drawable.ic_baseline_phone_24)
                    setDeleteIntent(pendingBroadcastIntent(2, REJECT, model))
                    getAction("Отклонить", 3, REJECT, model)
                    getAction("Ответить", 4, ANSWER, model)
                    setContentIntent(INCOMING_CALL.activityPendingIntent(model))
                }

                REJECT -> {
                    setCategory(CATEGORY_CALL)
                    setLargeIcon(this@notification)
                    setContentText(name)
                    setContentTitle(titles)
                    setSmallIcon(R.drawable.ic_baseline_phone_24)
                    setContentIntent(INCOMING_CALL.activityPendingIntent(model))
                }
            }
            priority = PRIORITY_MAX
        }.build()
    }

    private fun String.activityPendingIntent(model: RemoteMessageModel) = with(model) {
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

    private val RemoteMessageModel.id
        get() = if (typeMessage == MESSAGE) Random(99).nextInt() else callingId.toInt()

    private val RemoteMessageModel.titles
        get() = when (typeMessage) {
            MESSAGE -> {
                if (get.textMessage.startsWith(IS_RECORD.trim()))
                    "Голосовое сообщение от $name" else "Входящее сообщение от $name"
            }

            REJECT -> "Пропущенный вызов"
            INCOMING_CALL -> "Входящий вызов"
            else -> ""
        }

    private fun NotificationCompat.Builder.getAction(
        text: String, code: Int, action: String, model: RemoteMessageModel
    ) = addAction(1, text, pendingBroadcastIntent(code, action, model))

    private fun pendingBroadcastIntent(code: Int, action: String, model: RemoteMessageModel) =
        with(model) {
            val bundle = Bundle().apply { putString(CALLING_ID, callingId) }
            pendingIntentBroadcastBuilder.invoke(code, intentBuilder.invoke(action, bundle))
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(model: RemoteMessageModel) =
        notificationManager.getNotificationChannel(model.typeMessage)
            ?: notificationManager.createNotificationChannel(channel(model))

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel(model: RemoteMessageModel) = with(model) {
        NotificationChannel(typeMessage, typeMessage, IMPORTANCE_HIGH)
    }.apply { setSound(null, null) }

    private fun loadIcon(url: String, onLoad: (Bitmap) -> Unit, onFail: () -> Unit) {
        Glide.with(context).asBitmap()
            .load(url)
            .placeholder(R.drawable.ic_baseline_person_24)
            .circleCrop()
            .addListener(successListener(onLoad, onFail)).submit()
    }

    private fun successListener(onSuccess: (Bitmap) -> Unit, onFail: () -> Unit) =
        object : RequestListener<Bitmap> {

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ) = isFirstResource.apply { resource?.apply { onSuccess(this) } }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                onFail()
                return false
            }
        }

    private val currentTimestamp get() = Calendar.getInstance().time.time
}