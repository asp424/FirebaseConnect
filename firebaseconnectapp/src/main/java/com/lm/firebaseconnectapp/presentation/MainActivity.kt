package com.lm.firebaseconnectapp.presentation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.lm.firebaseconnect.log

@RequiresApi(Build.VERSION_CODES.P)
class MainActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
        "cr".log
    }

    override fun onResume() {
        super.onResume()
        "res".log
    }
}


