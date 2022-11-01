package com.lm.firebaseconnectapp.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnectapp.appComponent

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FBMessageService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        appComponent.firebaseMessageServiceCallback().sendCallBack(remoteMessage)
    }
}





