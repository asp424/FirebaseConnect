package com.lm.firebaseconnect

import com.lm.firebaseconnect.State.ANSWER
import com.lm.firebaseconnect.State.BUSY
import com.lm.firebaseconnect.State.CALLING_ID
import com.lm.firebaseconnect.State.CHECK_FOR_CALL
import com.lm.firebaseconnect.State.DATA
import com.lm.firebaseconnect.State.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.NAME
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.State.REJECT
import com.lm.firebaseconnect.State.RESET
import com.lm.firebaseconnect.State.TOKEN
import com.lm.firebaseconnect.State.TYPE_MESSAGE
import com.lm.firebaseconnect.State.WAIT
import com.lm.firebaseconnect.State.callState
import com.lm.firebaseconnect.State.remoteMessageModel
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteMessageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private fun sendRemoteMessage(inBox: JSONObject, token: String) {
        firebaseRead.readNode(Nodes.TOKEN, firebaseRead.firebaseSave.myDigit) { myToken ->
            fCMApi.sendRemoteMessage(
                JSONObject()
                    .put(DATA, inBox.put(TOKEN, myToken)).put(
                        TOKEN, JSONArray()
                            .put(token)
                    ).toString(), header
            )?.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {}
                override fun onFailure(call: Call<String?>, t: Throwable) {}
            })
        }
    }

    fun message(message: String) {
        firebaseRead.readNode(Nodes.TOKEN, firebaseRead.firebaseSave.firebaseChat.chatId) { token ->
            sendRemoteMessage(
                titleMessageType(
                    "Сообщение от ${firebaseRead.firebaseSave.myName}", message, MESSAGE
                ), token
            )
        }
    }

    fun call(token: String) {
        sendRemoteMessage(
            titleMessageType(
                "Входящий вызов",
                "Входящий вызов от ${firebaseRead.firebaseSave.myName}", CHECK_FOR_CALL
            ), token
        )
        callState.value = remoteMessageModel.outgoingCall
        CoroutineScope(IO).launch {
            delay(10000)
            if (callState.value.typeMessage == OUTGOING_CALL)
                callState.value = remoteMessageModel.rejectCall
        }
    }

    fun checkForCall(
        remoteMessageModel: RemoteMessageModel
    ) {
        remoteMessageModel.typeMessage.log
        callState.value.typeMessage.log
        if (callState.value.typeMessage == WAIT) {
            sendRemoteMessage(
                titleMessageType(
                    "Связь установлена",
                    "Ответ на входящий вызов. Я ${firebaseRead.firebaseSave.myName}",
                    GET_CHECK_FOR_CALL
                ), remoteMessageModel.token
            )
        } else sendRemoteMessage(
            titleMessageType(
                "Юзер занят",
                "Ответ на входящий вызов. Я ${firebaseRead.firebaseSave.myName}",
                BUSY
            ), remoteMessageModel.token
        )
    }

    fun callCallBack(remoteMessage: RemoteMessageModel) {
        sendRemoteMessage(
            titleMessageType(
                "Входящий вызов",
                "Входящий вызов от ${firebaseRead.firebaseSave.myName}",
                INCOMING_CALL
            ), remoteMessage.token
        )
        callState.value = remoteMessageModel.getIncomingCall
    }

    fun answer(token: String) {
        sendRemoteMessage(
            titleMessageType(
                "Взял трубу", "Взял трубу", ANSWER
            ), token
        )
    }

    fun reject(token: String) {
        sendRemoteMessage(titleMessageType(cancelCall.first, cancelCall.second, REJECT), token)
        callState.value = remoteMessageModel.rejectCall
    }

    fun reset(token: String) {
        sendRemoteMessage(titleMessageType(cancelCall.first, cancelCall.second, RESET), token)
        callState.value = remoteMessageModel.rejectCall
    }

    private val cancelCall
        get() = Pair(
            "Пропущенный вызов",
            "Вам звонил ${firebaseRead.firebaseSave.myName}"
        )

    private fun titleMessageType(title: String, message: String, typeMessage: String) =
        JSONObject().put(TYPE_MESSAGE, typeMessage)
            .put(NAME, title)
            .put(MESSAGE, message)
            .put(CALLING_ID, firebaseRead.firebaseSave.myDigit)

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
