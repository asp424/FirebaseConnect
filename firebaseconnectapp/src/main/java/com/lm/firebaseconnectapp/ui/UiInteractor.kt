package com.lm.firebaseconnectapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Ringtone
import android.net.Uri
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.core.IntentHandler
import com.lm.firebaseconnectapp.core.NotificationReceiver
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.data.one_tap_google.FBRegStates
import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.toast
import com.lm.firebaseconnectapp.ui.UiStates.getNavState
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setToolbarVisible
import com.lm.firebaseconnectapp.ui.navigation.NavHost
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.MainTheme
import javax.inject.Inject
import javax.inject.Named

interface UiInteractor {

    val SignInClient.regCallBack: (ActivityResult) -> Unit

    fun onCreate(intent: Intent, mainActivity: MainActivity)

    fun onNewIntent(intent: Intent)

    fun signOutFromGoogle(signInClient: SignInClient, onOut: () -> Unit)

    @SuppressLint("ComposableNaming")
    @RequiresApi(Build.VERSION_CODES.P)
    class Base @Inject constructor(
        private val firebaseConnect: FirebaseConnect,
        private val sPreferences: SPreferences,
        @Named("Ringtone")
        private val ringtone: Ringtone,
        private val oneTapGoogleAuth: OneTapGoogleAuth,
        private val notificationManager: NotificationManagerCompat,
        private val firebaseAuth: FirebaseAuth,
        private val intentHandler: IntentHandler,
        private val notificationReceiver: NotificationReceiver
    ) : UiInteractor {

        override fun onCreate(intent: Intent, mainActivity: MainActivity) {
            intentHandler.onCreate(intent, onEmptyIntent = {
                setDataForEmulator(NavRoutes.MAIN, 0, mainActivity)
                //setData(NavRoutes.MAIN, 0, mainActivity)
            }, onValidIntent = { setData(NavRoutes.CHAT, 1, mainActivity) })
        }

        override fun onNewIntent(intent: Intent) = intentHandler.onNewIntent(intent)

        override val SignInClient.regCallBack: (ActivityResult) -> Unit get() =
            { oneTapGoogleAuth.handleResult(it, this, saveData) { toast } }

        override fun signOutFromGoogle(signInClient: SignInClient, onOut: () -> Unit) {
            signInClient.signOut().addOnCompleteListener { if (it.isSuccessful) onOut() }
        }

        @RequiresApi(Build.VERSION_CODES.P)
        private fun start(startScreen: NavRoutes, flag: Int = 0, mainActivity: MainActivity) {
            notificationManager.cancelAll()
            if (flag == 0) setSavedChatModel
            mainActivity.setContent {
                MainTheme(
                    firebaseConnect, ringtone, sPreferences, firebaseAuth, oneTapGoogleAuth,
                    this, notificationReceiver
                ) { NavHost(startScreen); setNavController }
            }
        }

        private val setSavedChatModel get() = with(sPreferences.readChatId()) {
                if (isNotEmpty() && all { it.isDigit() }) firebaseConnect.setChatId(toInt())
            }

        private val setDataForEmulator: (NavRoutes, Int, MainActivity) -> Unit by lazy {
            { r, f, a ->
                firebaseConnect.setMyDigit("8")
                    .setAndSaveIcon(
                        "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png"
                            .toUri()
                    )
                    .setAndSaveName("Emulator")
                firebaseConnect.getAndSaveToken()
                start(r, f, a)
                setToolbarVisible(true)
            }
        }

        private val setNavController @Composable get() = with(mainDep) {
                    LaunchedEffect(getNavState) {
                        if (getNavState.route.isNotEmpty()) navController.navigate(getNavState.route)
                    }
                }

        private val setData: (NavRoutes, Int, MainActivity) -> Unit by lazy {
            { r, f, a -> getUid {
                    this?.also { id ->
                        firebaseConnect.setMyDigit(id).setIcon.setName
                        start(r, f, a)
                        setToolbarVisible(true)
                    } ?: start(NavRoutes.REG, mainActivity = a)
                }
            }
        }

        private val FirebaseConnect.setName get() = sPreferences.readName().also { name ->
            if (name.isEmpty()) setMyName("Empty name") else setMyName(name); getAndSaveToken()
                }

        private val FirebaseConnect.setIcon get() = apply {
                with(sPreferences) {
                    readIconUri()?.toString()?.takeIf { it.isNotEmpty() }?.apply {
                        setMyIcon(toString())
                    }
                }
            }

        private val saveData: FBRegStates.OnSuccess.() -> Unit by lazy {
            {
                getUid {
                    this?.also { id ->
                        sPreferences.saveMyId(id)
                        firebaseConnect.setMyDigit(id)
                        iconUri?.saveIconUri; name?.saveName
                        setNavState(NavRoutes.MAIN)
                        setToolbarVisible(true)
                    } ?: run { setNavState(NavRoutes.REG); setToolbarVisible(false) }
                }
            }
        }

        private val Uri?.saveIconUri get() = this?.also { uri ->
                sPreferences.saveIconUri(uri)
                firebaseConnect.setAndSaveIcon(uri)
            }

        private val String?.saveName get() = this?.also { name ->
                sPreferences.saveName(name)
                with(firebaseConnect) { setAndSaveName(name); getAndSaveToken() }
            }

        private val getUid: (String?.() -> Unit) -> Unit by lazy {
            {
                firebaseAuth.currentUser?.uid?.apply {
                    checkId?.also { myId -> it(myId) } ?: it(null)
                } ?: it(null)
            }
        }

        private val String.checkId get() =
            if (isNotEmpty()) filter { v -> v.isDigit() && v != '0' } else null
    }
}