package com.lm.firebaseconnect

import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.CALL
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.CHECK_FOR_CALL
import com.lm.firebaseconnect.States.DATA
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.NOTIFY_CALLBACK
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECTED_CALL
import com.lm.firebaseconnect.States.TITLE
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.TYPE_MESSAGE
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.RemoteMessageModel
import com.lm.firebaseconnect.models.UserModel
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
    private val apiKey: String, private val firebaseRead: FirebaseRead
) {

    fun message(message: String) {
        firebaseRead.readNode(Nodes.TOKEN, getChatId) { token ->
            sendRemoteMessage("Сообщение от $getMyName", message, MESSAGE, token)
        }
    }

    fun initialCall(userModel: UserModel) = with(userModel) {
        with(firebaseRead.firebaseSave) {
            save(CALL, Nodes.CALL, path = id.getPairPath, digit = id){
                remoteMessageModel.outgoingCall(id, name, token).set
                sendRemoteMessage(
                    "Входящий вызов", "Входящий вызов от $getMyName", CHECK_FOR_CALL, token
                )
                CoroutineScope(IO).launch {
                    delay(10000)
                    if (OUTGOING_CALL.isType) {
                        rejectCall
                        with(firebaseRead.firebaseSave) {
                            save(REJECTED_CALL, Nodes.CALL, path = id.getPairPath, digit = id)
                        }
                    }
                }
            }
        }

    }

    fun checkForCall(remoteMessageModel: RemoteMessageModel) {
        with(firebaseRead.firebaseSave) {
            databaseReference
                .child(firebaseChat.myDigit)
                .child(Nodes.CALL.node())
                .child(remoteMessageModel.callingId.getPairPath)
                .addListenerForSingleValueEvent(firebaseRead.valueListener
                    .singleEventListener() {
                        if (it.value.toString() != REJECTED_CALL) {
                            if (it.value.toString() == CALL) {
                                "call".log
                                if (WAIT.isType)
                                    sendRemoteMessage(
                                        "Связь установлена",
                                        "Ответ на входящий вызов. Я $getMyName",
                                        GET_CHECK_FOR_CALL, remoteMessageModel.token
                                    )
                                else sendRemoteMessage(
                                    "Юзер занят",
                                    "Ответ на входящий вызов. Я $getMyName", BUSY,
                                    remoteMessageModel.token
                                )
                            }
                        }
                })
        }
    }

fun doCall(remoteMessage: RemoteMessageModel) {
    sendRemoteMessage(
        "Входящий вызов", "Входящий вызов от $getMyName", INCOMING_CALL,
        remoteMessage.token
    ); remoteMessageModel.getIncomingCall.set
}

fun notifyCallback(token: String) {
    sendRemoteMessage(
        NOTIFY_CALLBACK, NOTIFY_CALLBACK, NOTIFY_CALLBACK,
        token
    )
}

fun answer(token: String) =
    sendRemoteMessage("Взял трубу", "Взял трубу", ANSWER, token)

fun cancelCall(token: String, typeMessage: String) =
    sendRemoteMessage(
        "Пропущенный вызов", "Вам звонил $getMyName", typeMessage, token
    ).apply { rejectCall }

val rejectCall get() = remoteMessageModel.rejectCall.set

private val getMyName get() = firebaseRead.firebaseSave.firebaseChat.myName

private val getMyIcon get() = firebaseRead.firebaseSave.firebaseChat.myIcon

private val getMyDigit get() = firebaseRead.firebaseSave.firebaseChat.myDigit

private val getChatId get() = firebaseRead.firebaseSave.firebaseChat.chatId

private fun sendRemoteMessage(
    title: String, message: String, typeMessage: String, token: String
) {
    firebaseRead.readNode(Nodes.TOKEN, getMyDigit) { myToken ->
        fCMApi.sendRemoteMessage(
            JSONObject().put(
                DATA, JSONObject().put(TYPE_MESSAGE, typeMessage)
                    .put(TITLE, title).put(MESSAGE, message)
                    .put(NAME, getMyName).put(ICON, getMyIcon)
                    .put(CALLING_ID, getMyDigit).put(TOKEN, myToken)
            ).put(TOKEN, JSONArray().put(token)).toString(), header
        )?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }
}

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
}
