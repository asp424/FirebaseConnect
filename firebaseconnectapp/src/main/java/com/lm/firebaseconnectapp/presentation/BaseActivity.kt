package com.lm.firebaseconnectapp.presentation

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.data.one_tap_google.FBRegStates
import com.lm.firebaseconnectapp.di.compose.MainDep
import com.lm.firebaseconnectapp.toast
import com.lm.firebaseconnectapp.ui.NavHost
import com.lm.firebaseconnectapp.ui.NavHostReg
import com.lm.firebaseconnectapp.ui.theme.MainTheme

@RequiresApi(Build.VERSION_CODES.P)
open class BaseActivity : ComponentActivity() {

    val regLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(), regCallBack)
    }

    private val regCallBack: (ActivityResult) -> Unit
        get() = {
            appComponent.oneTapGoogleAuth().handleResult(
                it, appComponent, lifecycleScope, signInClient, setBio
            ) { toast }
        }

    protected fun onCreate() {
       regLauncher
       // getUid?.apply {
            setBioToFBConnect.start()
      //  } ?:
        //setContent { MainDep(appComponent) { NavHostReg() } }
    }

    private fun FirebaseConnect.start() {
        appComponent.notificationManager().cancelAll();
        //getAndSaveToken();
        setContent()
    }

    private fun FirebaseConnect.setContent() {
        setContent { MainTheme(appComponent) { SetContent { NavHost() } } }
    }

    private val setBio: FBRegStates.OnSuccess.() -> Unit by lazy {
        { setIdToFBC.setMyName(setToPreferences); with(appComponent.uiStates()){ "main".setNavState } }
    }

    private val setIdToFBC
        get() = appComponent.fBConnect().apply {
            setMyDigit(getUid?.filter { v -> v.isDigit() } ?: "")
        }

    val signInClient by lazy { Identity.getSignInClient(this) }

    private val FBRegStates.OnSuccess.setToPreferences
        get() = appComponent.sPreferences().saveIconUri(iconUri).setName(name ?: "").getName()

    private val setBioToFBConnect
        get() = setIdToFBC.setMyName(appComponent.sPreferences().getName())

    private val getUid get() = appComponent.firebaseAuth().currentUser?.uid
}
