package com.lm.firebaseconnectapp.presentation

import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.data.one_tap_google.FBRegStates
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.toast
import com.lm.firebaseconnectapp.ui.NavHost
import com.lm.firebaseconnectapp.ui.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.MainTheme

@RequiresApi(Build.VERSION_CODES.P)
open class BaseActivity : ComponentActivity() {

    val regLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(), regCallBack)
    }

    private val regCallBack: (ActivityResult) -> Unit
        get() = {
            appComponent.oneTapGoogleAuth().handleResult(
                it, appComponent, lifecycleScope, signInClient, saveData
            ) { toast }
        }

    protected fun onCreate() {
        regLauncher; setData()
    }

    private fun start(startScreen: NavRoutes) = with(appComponent) {
        notificationManager().cancelAll()
        setContent {
            MainTheme(this) { NavHost(startScreen); NavController() }
        }
    }

    @Composable
    fun NavController() = with(appComponent.uiStates()) {
        with(mainDep) {
            LaunchedEffect(getNavState) {
                if (getNavState.route.isNotEmpty()
                    && navController.currentDestination?.route == NavRoutes.REG.route
                )
                    navController.navigate(getNavState.route)
            }
        }
    }

    private val setData: () -> Unit by lazy {
        {
            with(appComponent) {
                getUid {
                    this?.also { id ->
                        fBConnect().setMyDigit(id).setName()
                        start(NavRoutes.MAIN)
                        with(uiStates()) { true.setToolbarVisible }
                    } ?: start(NavRoutes.REG)
                }
            }
        }
    }

    private fun FirebaseConnect.setName() = with(appComponent.sPreferences()) {
        getName().also { name ->
            if (name.isEmpty()) {
                setMyName("Empty name"); showToast("Name is empty")
            } else {
                setMyName(name); showToast("Data set success")
            }
            getAndSaveToken()
        }
    }

    private val saveData: FBRegStates.OnSuccess.() -> Unit by lazy {
        {
            with(appComponent) {
                with(uiStates()) {
                    getUid {
                        this?.also { id ->
                            fBConnect().setMyDigit(id)
                            iconUri?.saveIconUri; name?.saveName
                            fBConnect().setName()
                            NavRoutes.MAIN.setNavState; true.setToolbarVisible
                        } ?: run { NavRoutes.REG.setNavState; false.setToolbarVisible }
                    }
                }
            }
        }
    }

    private val Uri?.saveIconUri
        get() = this?.apply {
            appComponent.sPreferences().saveIconUri(this)
            appComponent.fBConnect().saveIcon(this)
        } ?: showToast("Icon URI is null")

    private val String?.saveName
        get() = this?.also {
            appComponent.sPreferences().setName(it)
            appComponent.fBConnect().saveName(it)
        } ?: showToast("Name is null")

    private fun showToast(text: String) = toast(this, text)

    val signInClient by lazy { Identity.getSignInClient(this) }

    private val getUid: (String?.() -> Unit) -> Unit by lazy {
        {
            appComponent.firebaseAuth().currentUser?.uid?.apply {
                if (isNotEmpty()) filter { v -> v.isDigit() }
                    .filter { v -> v != '0' }.also { myId -> it(myId) }
                else {
                    it(null); showToast("Firebase uid is empty")
                }
            } ?: it(null).apply {
                showToast("Firebase uid is null")
            }
        }
    }
}
