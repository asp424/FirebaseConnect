package com.lm.firebaseconnect

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.lm.firebaseconnect.listeners.ChildEventListenerInstance
import com.lm.firebaseconnect.listeners.ValueEventListenerInstance
import com.lm.firebaseconnect.models.Nodes

class FirebaseConnect private constructor(
    cryptoKey: String,
    apiKey: String,
    var myName: String = "",
    var myDigit: String = "",
    var chatId: String = "",
) {

    class Instance(
        private val cryptoKey: String,
        private val apiKey: String,
    ) {
        fun init() = FirebaseConnect(cryptoKey, apiKey)
    }

    fun getAndSaveToken(onGet: (String) -> Unit = {}) = fcmProvider.getAndSaveToken { onGet(it) }

    fun setChatId(chatId: Int) = apply { this.chatId = chatId.toString() }

    fun setMyName(name: String) = apply { this.myName = name }

    fun setMyDigit(myDigit: String) = apply { this.myDigit = myDigit }

    fun setWriting() = firebaseSave.save(ONE, Nodes.WRITING)

    fun setNoWriting() = firebaseSave.save(ZERO, Nodes.WRITING)

    fun sendMessage(text: String) = firebaseSave.sendMessage(text, remoteMessages)

    fun deleteAllMessages() = firebaseSave.deleteAllMessages()

    @Composable
    fun SetChatContent(content: @Composable FirebaseConnect.() -> Unit) {
        rememberUpdatedState(LocalLifecycleOwner.current).value.apply {
            DisposableEffect(this) {
                with(firebaseRead) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (Lifecycle.Event.ON_CREATE == event) initStates()
                        if (Lifecycle.Event.ON_RESUME == event) startListener()
                        if (Lifecycle.Event.ON_PAUSE == event) onPause()
                    }
                    lifecycle.addObserver(observer)
                    onDispose { onPause(); lifecycle.removeObserver(observer) }
                }
            }
            content(this@FirebaseConnect)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun SetContent(
        content: @Composable FirebaseConnect.() -> Unit
    ) {
        val context = LocalContext.current
        with(firebaseRead) {
            val observer = LifecycleEventObserver { _, event ->
                if (Lifecycle.Event.ON_CREATE == event) initStates()
                if (Lifecycle.Event.ON_RESUME == event) firebaseHandler.startMainListener()
                if (Lifecycle.Event.ON_PAUSE == event) firebaseHandler.stopMainListener()
            }
            val activity =
                remember { context.getActivity()?.apply { lifecycle.addObserver(observer) } }
            rememberUpdatedState(activity).value.apply {
                DisposableEffect(this) {
                    onDispose {}
                }
            }
            content(this@FirebaseConnect)
        }
    }

    private val firebaseSave by lazy {
        FirebaseSave(myDigit, this, timeConverter, myName, crypto)
    }

    private val timeConverter by lazy { TimeConverter() }

    private val childEventListenerInstance by lazy { ChildEventListenerInstance() }

    private val valueEventListenerInstance by lazy { ValueEventListenerInstance(firebaseSave) }

    private val firebaseHandler by lazy {
        FirebaseHandler(this, firebaseSave, childEventListenerInstance, firebaseRead)
    }

    private val fcmProvider by lazy { FCMProvider(myDigit) }

    private val crypto by lazy { Crypto(cryptoKey) }

    private val firebaseRead by lazy { FirebaseRead(firebaseSave, valueEventListenerInstance) }

    val remoteMessages by lazy { RemoteMessages(apiKey, firebaseRead) }

    companion object {
        const val ZERO = "0"
        const val ONE = "1"
    }
}

