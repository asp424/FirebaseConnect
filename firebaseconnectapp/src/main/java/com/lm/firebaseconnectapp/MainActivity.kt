package com.lm.firebaseconnectapp

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.data.one_tap_google.FBAuth
import com.lm.firebaseconnectapp.data.one_tap_google.FBRegStates
import com.lm.firebaseconnectapp.data.one_tap_google.OTGRegState
import com.lm.firebaseconnectapp.data.one_tap_google.OneTapGoogleAuth
import com.lm.firebaseconnectapp.di.compose.MainScreenDependencies
import com.lm.firebaseconnectapp.ui.NavHost
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : ComponentActivity() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val oneTapGoogleAuth by lazy {
        OneTapGoogleAuth.Base(
            Identity.getSignInClient(this@MainActivity), FBAuth.Base(firebaseAuth)
        )
    }

    private val sharedPreferences by lazy { getSharedPreferences("checkForFirst", MODE_PRIVATE) }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (firebaseAuth.currentUser?.uid != null) {
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult())
            { handleResult(it) }.apply { startOTGAuth(this) }
        } else {
            with(appComponent) {
                notificationManager().cancelAll()
                firebaseConnect().getAndSaveToken()
                setContent { MainScreenDependencies(this) { NavHost() } }
            }
        }
    }

    private fun startOTGAuth(
        regLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) = oneTapGoogleAuth.startAuth {
        when (it) {
            is OTGRegState.OnSuccess ->
                regLauncher.launch(IntentSenderRequest.Builder(it.intentSender).build())
            is OTGRegState.OnError -> {
                longToast(it.message)
            }
        }
    }

    private fun handleResult(result: ActivityResult) =
        lifecycleScope.launch(IO) {
            launch(Main) {
                delay(20000L); longToast("Authorize error, try later")
            }
            oneTapGoogleAuth.handleResultAndFBReg(result).collect {
                when (it) {
                    is FBRegStates.OnSuccess -> saveIconUri(it.iconUri)
                    is FBRegStates.OnError -> longToast(it.message)
                    is FBRegStates.OnClose -> {
                        delay(1000)
                    }
                }
            }
        }

    private fun saveIconUri(uri: Uri?) {
        sharedPreferences.edit()
            .putString(Uri.EMPTY.toString(), uri.toString()).apply()
    }

    fun readIconUri() = sharedPreferences
        .getString(Uri.EMPTY.toString(), "")?.toUri()
}


