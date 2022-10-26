package com.lm.firebaseconnectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.ui.NavHost

val firebaseConnectInstance by lazy {
    FirebaseConnect.Instance(
        BuildConfig.C_KEY, BuildConfig.FCM_SERVER_KEY, 23, "hui"
    )
}

val firebaseConnect by lazy { firebaseConnectInstance.chat() }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseConnectInstance.getAndSaveToken(); setContent { NavHost() }
    }

    override fun onResume() {
        super.onResume()
        firebaseConnect.startMainListener()
        firebaseConnect.startListenerForCall()
    }

    override fun onPause() {
        super.onPause()
        firebaseConnect.stopListenerForCall()
        firebaseConnect.stopMainListener()
    }
}


