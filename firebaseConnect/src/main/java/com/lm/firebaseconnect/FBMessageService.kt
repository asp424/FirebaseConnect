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

    private val sharedPreferences by lazy { getSharedPreferences("checkForFirst", MODE_PRIVATE) }



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

        firebaseMessageServiceChatCallback
            .sendCallBack(
                remoteMessageModel.getFromRemoteMessage(remoteMessage),
                activityManager,
                packageName,
                notificationManager, notificationBuilder, sharedPreferences
            )
    }
}





