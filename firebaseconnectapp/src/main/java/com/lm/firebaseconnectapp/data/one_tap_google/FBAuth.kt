package com.lm.firebaseconnectapp.data.one_tap_google

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface FBAuth {

    fun startAuthWithGoogleId(googleIdToken: String): Flow<FBRegStates>

    class Base @Inject constructor(
        private val firebaseAuth: FirebaseAuth
    ) : FBAuth {

        override fun startAuthWithGoogleId(googleIdToken: String) = callbackFlow {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    trySend(
                    if (task.isSuccessful) FBRegStates.OnSuccess(Uri.EMPTY)
                    else FBRegStates.OnError(task.exception?.message ?: "null")
                    )
                    close()
                }.addOnFailureListener { trySend(FBRegStates.OnError(it.message ?: "null"))
                    close() }
                .addOnCanceledListener { trySend(FBRegStates.OnError("cancelled"))
                    close() }
            awaitClose()
        }.flowOn(IO)
    }
}