package com.lm.firebaseconnectapp.data.one_tap_google

import android.app.Activity.RESULT_OK
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.CANCELED
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.BuildConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface OneTapGoogleAuth {

    fun handleResultAndFBReg(result: ActivityResult): Flow<FBRegStates>

    fun startAuth(onResult: (OTGRegState) -> Unit)

    class Base @Inject constructor(
        private val signInClient: SignInClient,
        private val fbAuth: FBAuth
    ) : OneTapGoogleAuth {

        override fun handleResultAndFBReg(result: ActivityResult) = callbackFlow {
            if (result.resultCode == RESULT_OK) {
                try {
                    signInClient.getSignInCredentialFromIntent(result.data).apply {
                        val token = googleIdToken
                        if (token.isNullOrEmpty()) trySendBlocking(FBRegStates.OnError("null"))
                        else launch {
                            fbAuth.startAuthWithGoogleId(token).collect {
                                trySend(
                                    when (it) {
                                        is FBRegStates.OnSuccess -> FBRegStates.OnSuccess(
                                            profilePictureUri
                                        )
                                        is FBRegStates.OnError -> FBRegStates.OnError(it.message)
                                        is FBRegStates.OnClose -> FBRegStates.OnClose("close")
                                    }
                                )
                                close()
                            }
                        }
                    }
                } catch(e: ApiException){
                    trySend(
                        when (e.statusCode) {
                            CANCELED ->  FBRegStates.OnClose(e.message?: "cancelled")
                            NETWORK_ERROR ->  FBRegStates.OnClose(e.message?: "network error")
                            else -> FBRegStates.OnError(e.message?: "error")
                        }
                    )
                    close(e)
                }
            }
            awaitClose()
        }.flowOn(IO)

        override fun startAuth(onResult: (OTGRegState) -> Unit) {
            signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    onResult(OTGRegState.OnSuccess(result.pendingIntent.intentSender))
                }
                .addOnFailureListener {
                    onResult(OTGRegState.OnError(it.localizedMessage ?: "null"))
                }
        }

        private val requestOptions by lazy {
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .build()
        }

        private val signInRequest by lazy {
            BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(requestOptions)
                .build()
        }
    }
}