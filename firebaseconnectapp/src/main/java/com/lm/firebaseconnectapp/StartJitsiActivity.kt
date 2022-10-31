package com.lm.firebaseconnectapp

import android.os.Bundle
import androidx.core.app.ComponentActivity

class StartJitsiActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startJitsiMit(this, "a")
    }
}