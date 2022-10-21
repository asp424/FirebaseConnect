package com.lm.firebaseconnect

import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

class FCMProvider(
    private val databaseUserId: String,
    private val apiKey: String,
    private val firebaseRead: FirebaseRead,
    private val token: String
) {

    fun getAndSaveToken(onGet: (String) -> Unit) = fcm.token.addOnCompleteListener {
        if (it.isSuccessful) {
            it.result.toString().also { token -> saveToken(token); onGet(token) }
        }
    }

    private fun sendChatRemoteMessageOnInternalToken(message: String) = with(firebaseRead) {
        startReadToken {
            sendRemoteMessage(
                it, firebaseSave.myName, message, firebaseSave.pairPath,
                firebaseSave.himDigit, databaseUserId
            )
        }
    }

    private fun sendChatRemoteMessageOnExternalToken(message: String) = with(firebaseRead) {
        sendRemoteMessage(
            token, firebaseSave.myName, message, firebaseSave.pairPath,
            firebaseSave.himDigit, databaseUserId
        )
    }

    fun sendChatRemoteMessage(message: String){
        if (token.isNotEmpty()) sendChatRemoteMessageOnExternalToken(message)
        else sendChatRemoteMessageOnInternalToken(message)
    }

    private fun sendRemoteMessage(
        token: String,
        myName: String,
        message: String,
        node: String,
        receiverNode: String,
        userId: String
    ) {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "key=${apiKey}"
        headers["Content-Type"] = "application/json"
        fCMApi.sendRemoteMessage(
            JSONObject().put(
                "data",
                JSONObject().put("name", myName)
                    .put("message", message)
                    .put("chatNode", node)
                    .put("receiverNode", receiverNode)
                    .put("userId", userId)
            ).put("registration_ids", JSONArray().put(token)).toString(), headers
        )?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}

            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }

    private val fCMApi: ApiInterface by lazy {
        Retrofit.Builder().baseUrl("https://fcm.googleapis.com/fcm/")
            .addConverterFactory(ScalarsConverterFactory.create()).build()
            .create(ApiInterface::class.java)
    }

    interface ApiInterface {
        @POST("send")
        fun sendRemoteMessage(
            @Body remoteBody: String?, @HeaderMap headers: HashMap<String, String>
        ): Call<String?>?
    }

    private val fcm by lazy { FirebaseMessaging.getInstance() }

    private fun saveToken(token: String) = with(firebaseRead) {
        firebaseSave.databaseReference.child(firebaseSave.myDigit)
            .child(Nodes.TOKEN.node())
            .updateChildren(mapOf(firebaseSave.myDigit to token))
    }
}
