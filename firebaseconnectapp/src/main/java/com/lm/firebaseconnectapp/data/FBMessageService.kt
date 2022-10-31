package com.lm.firebaseconnectapp.data

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnectapp.appComponent

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FBMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        appComponent.firebaseMessageServiceCallback().sendCallBack(remoteMessage)
    }
}





