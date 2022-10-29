package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.API_KEY
import com.lm.firebaseconnect.State.CALLING_ID
import com.lm.firebaseconnect.State.CHAT_ID
import com.lm.firebaseconnect.State.CHAT_PATH
import com.lm.firebaseconnect.State.DATA
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.NAME
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.ROOM
import com.lm.firebaseconnect.State.TOKEN
import com.lm.firebaseconnect.State.TYPE_MESSAGE
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

class RemoteMessages(
    private val apiKey: String,
    private val firebaseRead: FirebaseRead
) {

    private fun sendRemoteMessage(inBox: JSONObject) {
        firebaseRead.readNode(Nodes.TOKEN) { token ->
            firebaseRead.readMyNode(Nodes.TOKEN) { myToken ->
                fCMApi.sendRemoteMessage(
                    JSONObject()
                        .put(
                            DATA, inBox.put(TOKEN, myToken).put(API_KEY, apiKey)
                        )
                        .put(
                            TOKEN, JSONArray().put(token)
                        ).toString(), header
                )?.enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {}
                    override fun onFailure(call: Call<String?>, t: Throwable) {}
                })
            }
        }
    }

    private fun sendCallMessage(inBox: JSONObject, token: String) {
            firebaseRead.readMyNode(Nodes.TOKEN) { myToken ->
                fCMApi.sendRemoteMessage(
                    JSONObject()
                        .put(
                            DATA, inBox.put(TOKEN, myToken).put(API_KEY, apiKey)
                        )
                        .put(
                            TOKEN, JSONArray().put(token)
                        ).toString(), header
                )?.enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {}
                    override fun onFailure(call: Call<String?>, t: Throwable) {}
                })
            }
    }

    fun message(message: String) = sendRemoteMessage(messageInbox.put(MESSAGE, message))

    fun call(token: String) = sendCallMessage(callInbox, token)

    fun answer(token: String) = sendCallMessage(answerInbox, token)

    fun reject(token: String) {
        sendCallMessage(rejectInbox, token)
        callState.value = remoteMessageModel.rejectCall
        //firebaseRead.firebaseSave.saveWait()
    }
    fun reset(token: String) {
        sendCallMessage(resetInbox, token)
        callState.value = remoteMessageModel.rejectCall
       // firebaseRead.firebaseSave.saveWait()
    }

    private val callInbox: JSONObject get() = testInbox
        .put(TYPE_MESSAGE, INCOMING_CALL)
        .put(ROOM, "room")

    private val rejectInbox: JSONObject get() = baseInbox.put(TYPE_MESSAGE, REJECT)

    private val answerInbox: JSONObject get() = baseInbox.put(TYPE_MESSAGE, ANSWER)

    private val resetInbox: JSONObject get() = baseInbox.put(TYPE_MESSAGE, RESET)

    private val messageInbox: JSONObject get() = baseInbox.put(TYPE_MESSAGE, MESSAGE)

    private val baseInbox: JSONObject get() = with(firebaseRead.firebaseSave) {
            JSONObject()
                .put(NAME, myName)
                .put(CHAT_PATH, pairPath)
                .put(CHAT_ID, firebaseChat.chatId)
                .put(CALLING_ID, firebaseRead.firebaseSave.myDigit)
        }

    private val testInbox: JSONObject get() = with(firebaseRead.firebaseSave) {
        JSONObject()
            .put(NAME, myName)
            .put(CALLING_ID, firebaseRead.firebaseSave.myDigit)
    }

    private val header by lazy {
        HashMap<String, String>().apply {
            put("Authorization", "key=${apiKey}")
            put("Content-Type", "application/json")
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
}