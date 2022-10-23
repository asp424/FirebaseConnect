package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class FCMProvider(
    private val myDigit: String
) {
    fun getAndSaveToken(onGet: (String) -> Unit) = with(fcm.token) {
        addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toString().also { token ->
                    databaseReference.child(myDigit)
                        .child(Nodes.TOKEN.node())
                        .updateChildren(mapOf(myDigit to token))
                    onGet(token)
                }
            }
        }
        Unit
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val fcm by lazy { FirebaseMessaging.getInstance() }
}
