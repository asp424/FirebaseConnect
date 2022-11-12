package com.lm.firebaseconnect

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.lm.firebaseconnect.States.DATA
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.models.Nodes
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
    private val firebaseConnect: FirebaseConnect, private val apiKey: String
) {
    fun getAndSaveToken(onGet: (String) -> Unit) = with(fcm.token) {
        addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toString().also { token ->
                    databaseReference.child(firebaseConnect.myDigit)
                        .child(Nodes.TOKEN.node())
                        .updateChildren(mapOf(firebaseConnect.myDigit to token))
                    onGet(token)
                }
            }
        }
        Unit
    }

    private fun JSONObject.serviceInbox(token: String) =
        put(TOKEN, JSONArray().put(token)).toString()

    fun send(remoteBody: JSONObject, token: String) =
        fCMApi.sendRemoteMessage(
            JSONObject().put(DATA, remoteBody).serviceInbox(token), header
        )
            ?.enqueue(responseCallback)

    private val header by lazy {
        HashMap<String, String>().apply {
            put("Authorization", "key=${apiKey}"); put("Content-Type", "application/json")
        }
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

    private val responseCallback by lazy {
        object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        }
    }

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val fcm by lazy { FirebaseMessaging.getInstance() }
}
