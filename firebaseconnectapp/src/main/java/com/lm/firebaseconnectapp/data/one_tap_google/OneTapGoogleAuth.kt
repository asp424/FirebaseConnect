package com.lm.firebaseconnectapp.data.one_tap_google

import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.CANCELED
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import com.lm.firebaseconnectapp.BuildConfig
import com.lm.firebaseconnectapp.di.dagger.AppComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface OneTapGoogleAuth {

    fun handleResultAndFBReg(result: ActivityResult, signInClient: SignInClient): Flow<FBRegStates>

    fun startAuth(signInClient: SignInClient, onResult: (OTGRegState) -> Unit)

    fun startOTGAuth(
        regLauncher: ActivityResultLauncher<IntentSenderRequest>, oneTapGoogleAuth: OneTapGoogleAuth,
        signInClient: SignInClient, onError: String.() -> Unit
    )

    fun handleResult(
        result: ActivityResult,
        signInClient: SignInClient,
        onSuccess: FBRegStates.OnSuccess.() -> Unit,
        onError: String.() -> Unit
    ): Job

    class Base @Inject constructor(private val fbAuth: FBAuth) : OneTapGoogleAuth {

        override fun handleResultAndFBReg(result: ActivityResult, signInClient: SignInClient) =
            callbackFlow {
                if (result.resultCode == RESULT_OK) {
                    try {
                        signInClient.getSignInCredentialFromIntent(result.data).apply {
                            val token = googleIdToken
                            if (token.isNullOrEmpty())
                                trySendBlocking(FBRegStates.OnError("Token is null"))
                            else launch {
                                fbAuth.startAuthWithGoogleId(token).collect {
                                    trySend(
                                        when (it) {
                                            is FBRegStates.OnSuccess ->
                                                FBRegStates.OnSuccess(
                                                profilePictureUri, displayName
                                            )
                                            is FBRegStates.OnError ->
                                                FBRegStates.OnError(it.message)
                                            is FBRegStates.OnClose ->
                                                FBRegStates.OnClose("close")
                                        }
                                    )
                                    close()
                                }
                            }
                        }
                    } catch (e: ApiException) {
                        trySend(
                            when (e.statusCode) {
                                CANCELED -> FBRegStates.OnClose(e.message ?: "Cancelled")
                                NETWORK_ERROR -> FBRegStates.OnClose(e.message ?: "Network error")
                                else -> FBRegStates.OnError(e.message ?: "Error message is null")
                            }
                        )
                        close(e)
                    }
                } else trySend(FBRegStates.OnError("Activity result in not OK"))
                awaitClose()
            }.flowOn(IO)

        override fun startAuth(
            signInClient: SignInClient, onResult: (OTGRegState) -> Unit
        ) {
            signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    onResult(OTGRegState.OnSuccess(result.pendingIntent.intentSender))
                }
                .addOnFailureListener {
                    onResult(OTGRegState.OnError(it.localizedMessage ?: "Error message is null"))
                }
        }

        override fun startOTGAuth(
            regLauncher: ActivityResultLauncher<IntentSenderRequest>, oneTapGoogleAuth: OneTapGoogleAuth,
            signInClient: SignInClient, onError: String.() -> Unit
        ) = oneTapGoogleAuth.startAuth(signInClient) {
            when (it) {
                is OTGRegState.OnSuccess -> regLauncher.launch(
                    IntentSenderRequest.Builder(it.intentSender).build()
                )
                is OTGRegState.OnError -> onError(it.message)
            }
        }

        @RequiresApi(Build.VERSION_CODES.P)
        override fun handleResult(
            result: ActivityResult,
            signInClient: SignInClient,
            onSuccess: FBRegStates.OnSuccess.() -> Unit,
            onError: String.() -> Unit
        ): Job =
            CoroutineScope(Main).launch {
                handleResultAndFBReg(result, signInClient).collect {
                    when (it) {
                        is FBRegStates.OnSuccess -> onSuccess(it)
                        is FBRegStates.OnError -> withContext(Main) { onError(it.message) }
                        is FBRegStates.OnClose -> delay(1000)
                    }
                }
            }

        private val requestOptions by lazy {
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        }

        private val signInRequest by lazy {
            BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(requestOptions)
                .build()
        }
    }
}