package com.lm.firebaseconnectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.ui.NavHost

val firebaseConnect by lazy {
    FirebaseConnect.Instance(
        BuildConfig.C_KEY, BuildConfig.FCM_SERVER_KEY, 23, "her"
    )
}

val firebaseChat by lazy { firebaseConnect.chat().setChatId(90) }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseConnect.getAndSaveToken()
        setContent { NavHost() }
    }

    override fun onResume() {
        super.onResume()
        firebaseChat.setOnlineApp()
        firebaseChat.startListenerForCall()
    }

    override fun onPause() {
        super.onPause()
        firebaseChat.setOfflineApp()
        firebaseChat.stopListenerForCall()
    }
}


