package com.lm.firebaseconnectapp.service

import android.content.Context
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.CALL_STATE
import com.lm.firebaseconnect.States.CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.INCOMING_CALL
import com.lm.firebaseconnect.States.MESSAGE
import com.lm.firebaseconnect.States.NOTIFY_CALLBACK
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.isType
import com.lm.firebaseconnect.States.notifyState
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnectapp.notifications.Notifications
import com.lm.firebaseconnectapp.startJitsiMit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Named

class FirebaseMessageServiceCallback(
    private val context: Context,
    val firebaseConnect: FirebaseConnect,
    private val notifications: Notifications,
    private val notificationManager: NotificationManagerCompat,
    @Named("Ringtone") private val ringtone: Ringtone,
    @Named("Notify") private val notificationSound: Ringtone,
    @Named("isRun") private val appIsInForeground: () -> Boolean
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCallBack(remoteMessage: RemoteMessage) = with(remoteMessageModel) {
        getFromRemoteMessage(remoteMessage).also { model ->
            with(notifications) {
                with(firebaseConnect.remoteMessages) {
                    firebaseRead.firebaseSave.save(
                        model.typeMessage, Nodes.CALL,
                        path = CALL_STATE,
                        digit = model.destinationId
                    )
                    when (model.typeMessage) {

                        MESSAGE -> {
                            if (!appIsInForeground() || firebaseConnect.chatId != model.callingId) {
                                showNotification(model, onShow = {
                                    notificationSound.play()
                                    callMessage(NOTIFY_CALLBACK, model.destinationId, model.token)
                                })
                            }
                        }

                        CHECK_FOR_CALL -> checkForCall(model)

                        REJECT -> {
                            if (!appIsInForeground()) {
                                model.set
                                notificationManager.cancel(get.callingId.toInt())
                                showNotification(model, onShow = {
                                    notificationSound.play()
                                    rejectCall
                                }, onFail = {
                                    rejectCall
                                })
                            } else rejectCall
                            ringtone.stop()
                        }

                        ANSWER -> {
                            CoroutineScope(Main).launch {
                                remoteMessageModel.answer
                                delay(1000)
                                firebaseRead.readNode(Nodes.TOKEN, getMyDigit) {
                                    startJitsiMit(
                                        context,
                                        it,
                                        firebaseConnect.myName,
                                        firebaseConnect.myIcon
                                    )
                                }
                            }
                        }

                        RESET -> rejectCall

                        GET_CHECK_FOR_CALL -> if (OUTGOING_CALL.isType) {
                            doCall(); model.set
                        }

                        INCOMING_CALL -> {
                            model.set
                            if (!appIsInForeground()) showNotification(model, onShow = {
                                ringtone.play()
                            })
                            ringtone.play()
                        }

                        GET_INCOMING_CALL -> {
                            if (OUTGOING_CALL.isType) model.set
                        }

                        NOTIFY_CALLBACK -> {
                            CoroutineScope(IO).launch {
                                notifyState.value = true
                                delay(3000)
                                notifyState.value = false
                            }
                        }

                        BUSY -> {
                            CoroutineScope(IO).launch {
                                model.set
                                delay(1000)
                                rejectCall
                            }
                        }
                    }
                }
            }
        }
    }
}