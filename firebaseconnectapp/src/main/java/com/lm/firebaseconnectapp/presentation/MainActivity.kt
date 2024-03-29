package com.lm.firebaseconnectapp.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.google.android.gms.auth.api.identity.Identity
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.core.Permissions
import com.lm.firebaseconnectapp.ui.UiInteractor
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.P)
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var uiInteractor: UiInteractor

    @Inject
    lateinit var permissions: Permissions

    val signInClient by lazy { Identity.getSignInClient(this) }

    val googleOneTapLauncher by lazy {
        with(uiInteractor) {
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
                signInClient.regCallBack(this@MainActivity)
            )
        }
    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.GetContent())
    {

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   takeImageResult.launch("image/*")
        appComponent.inject(this)
        googleOneTapLauncher
        uiInteractor.onCreate(intent, this)
        permissions.launchIfHasPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply { uiInteractor.onNewIntent(intent) }
    }
}


