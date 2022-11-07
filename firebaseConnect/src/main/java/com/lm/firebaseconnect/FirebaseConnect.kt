package com.lm.firebaseconnect

import android.annotation.SuppressLint
import android.net.Uri
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
    var myName: String = "hui",
    var myDigit: String = "555",
    var chatId: String = "666",
    var myIcon: String = "777",
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

    fun setMyIcon(myIcon: String) = apply { this.myIcon = myIcon }

    fun setAndSaveName(name: String) {
        firebaseSave.save(name, Nodes.NAME, path = myDigit)
        setMyName(name)
    }

    fun setAndSaveIcon(uri: Uri) = apply {
        firebaseSave.save(uri.toString(), Nodes.ICON, path = myDigit)
        setMyIcon(uri.toString())
    }

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
                        if (Lifecycle.Event.ON_CREATE == event) {
                            initStates()
                            firebaseHandler.startMainListener()
                        }
                        if (Lifecycle.Event.ON_RESUME == event) {
                            startListener()
                        }
                        if (Lifecycle.Event.ON_PAUSE == event) {
                            onPause()
                            firebaseSave.save(ZERO, Nodes.ONLINE, firebaseSave.firebaseChat.myDigit)
                        }
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
                if (Lifecycle.Event.ON_PAUSE == event) {
                    firebaseHandler.stopMainListener()
                }
            }
            val activity =
                remember { context.getActivity()?.apply { lifecycle.addObserver(observer) } }
            rememberUpdatedState(activity).value.apply {
                DisposableEffect(this) {
                    onDispose { activity?.lifecycle?.removeObserver(observer) }
                }
            }
            content(this@FirebaseConnect)
        }
    }

    val firebaseSave by lazy {
        FirebaseSave(this, timeConverter, crypto)
    }

    private val timeConverter by lazy { TimeConverter() }

    private val childEventListenerInstance by lazy { ChildEventListenerInstance() }

    private val valueEventListenerInstance by lazy { ValueEventListenerInstance(firebaseSave) }

    val firebaseHandler by lazy {
        FirebaseHandler(firebaseSave, childEventListenerInstance, firebaseRead)
    }

    private val fcmProvider by lazy { FCMProvider(myDigit, apiKey) }

    private val crypto by lazy { Crypto(cryptoKey) }

    private val firebaseRead by lazy { FirebaseRead(firebaseSave, valueEventListenerInstance) }

    val remoteMessages by lazy { RemoteMessages(firebaseRead, fcmProvider) }

    companion object {
        const val ZERO = "0"
        const val ONE = "1"
    }
}

