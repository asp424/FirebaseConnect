package com.lm.firebaseconnectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.ui.NavHost

val firebaseConnect by lazy {
    FirebaseConnect.Instance(
        BuildConfig.C_KEY, BuildConfig.FCM_SERVER_KEY, 80, "Хth"
    ).init()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseConnect.getAndSaveToken()
        setContent { NavHost() }
    }
}


