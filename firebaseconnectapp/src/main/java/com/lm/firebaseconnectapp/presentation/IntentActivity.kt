package com.lm.firebaseconnectapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.TOKEN

class IntentActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java).apply {
            action = intent?.action
            putExtra(NAME, getValue(NAME))
            putExtra(CALLING_ID, getValue(CALLING_ID))
            putExtra(TOKEN, getValue(TOKEN))
            putExtra(ICON, getValue(ICON))
        })
        finish()
    }
    private fun getValue(key: String) = intent?.getStringExtra(key) ?: ""
}