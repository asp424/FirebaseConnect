package com.lm.firebaseconnectapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.ui.UiInteractor
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.P)
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var uiInteractor: UiInteractor

    val signInClient by lazy { Identity.getSignInClient(this) }

    val googleOneTapLauncher by lazy {
        with(uiInteractor) {
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(), signInClient.regCallBack
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        googleOneTapLauncher
        uiInteractor.onCreate(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply { uiInteractor.onNewIntent(intent) }
    }
}


