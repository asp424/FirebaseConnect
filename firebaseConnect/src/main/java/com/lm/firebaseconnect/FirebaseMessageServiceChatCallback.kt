package com.lm.firebaseconnect

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.lm.firebaseconnect.FirebaseRead.Companion.RING
import com.lm.firebaseconnect.State.DATA
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.State.INCOMING_CALL
import com.lm.firebaseconnect.State.MESSAGE
import com.lm.firebaseconnect.State.OUTGOING_CALL
import com.lm.firebaseconnect.State.REJECT
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

internal class FirebaseMessageServiceChatCallback() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(
        remoteMessageModel: RemoteMessageModel,
        activityManager: ActivityManager,
        packageName: String,
        notificationManager: NotificationManagerCompat,
        notificationBuilder: Builder,
        sharedPreferences: SharedPreferences
    ) = with(remoteMessageModel) {
        when (typeMessage) {
            MESSAGE -> {
                if (!isRun(activityManager, packageName)) {
                    showNotificationFromMessenger(
                        textMessage, name, notificationBuilder, notificationManager,
                        callingId, "0", "Message"
                    )
                    path(chatId, Nodes.NOTIFY).updateChildren(mapOf(chatPath to RING))
                }
                callState.value = remoteMessageModel
            }
            INCOMING_CALL -> {
                if (!isRun(activityManager, packageName)) {
                    showNotificationFromMessenger(
                        "Вам звонок от $name", "Входящий вызов",
                        notificationBuilder, notificationManager,
                        callingId, "1", "IncomingCall"
                    )
                }
                sendRemoteMessage(token, apiKey, GET_INCOMING_CALL)
                    callState.value = remoteMessageModel
            }

            REJECT -> {
                notificationManager.cancel(callingId.toInt())
                showNotificationFromMessenger(
                    "Вам звонил $name", "Пропущенный вызов",
                    notificationBuilder, notificationManager,
                    callingId, "2", "MissingCall"
                )
                callState.value = remoteMessageModel

            }
            GET_INCOMING_CALL -> {
                if (callState.value.typeMessage == OUTGOING_CALL)
                    callState.value = remoteMessageModel
            }
            else -> callState.value = remoteMessageModel
        }
    }

    private fun SharedPreferences.save(value: String) = edit().putString("callState", value).apply()

    private fun SharedPreferences.read() = getString("callState", "")

    private fun path(child: String, node: Nodes) = databaseReference.child(child).child(node.node())

    private fun isRun(activityManager: ActivityManager, packageName: String): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance ==
                ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND && i.processName == packageName
            ) return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationFromMessenger(
        message: String,
        name: String,
        notificationBuilder: Builder,
        notificationManager: NotificationManagerCompat,
        id: String,
        channelId: String,
        channelName: String
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        )
        notificationManager.notify(
            id.toInt(), notificationBuilder
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        )
    }

    private fun sendRemoteMessage(token: String, apiKey: String, typeMessage: String) {
        fCMApi.sendRemoteMessage(
            JSONObject()
                .put(
                    DATA, JSONObject().put(TYPE_MESSAGE, typeMessage)
                )
                .put(TOKEN, JSONArray().put(token)).toString(), header.invoke(apiKey)
        )?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }

    private val header: (String) -> HashMap<String, String> by lazy {
        {
            HashMap<String, String>().apply {
                put("Authorization", "key=${it}")
                put("Content-Type", "application/json")
            }
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

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
}