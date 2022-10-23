package com.lm.firebaseconnect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class FirebaseConnect private constructor(
    cryptoKey: String,
    myName: String,
    myDigit: String,
    apiKey: String
) {

    var chatId = ""

    var databaseUserId = ""

    class Instance(
        private val cryptoKey: String,
        private val apiKey: String,
        private val myDigit: Int,
        private val myName: String
    ) {

        fun getAndSaveToken(onGet: (String) -> Unit = {}) =
            fcmProvider.getAndSaveToken { onGet(it) }

        fun chat() = FirebaseConnect(cryptoKey, myName, myDigit.toString(), apiKey)

        private val fcmProvider by lazy { FCMProvider(myDigit.toString()) }
    }

    fun setChatId(chatId: Int) = apply { this.chatId = chatId.toString() }

    fun setDatabaseUserId(databaseUserId: String) = apply { this.databaseUserId = databaseUserId }

    fun setWriting() = firebaseSave.saveWriting(ONE)

    fun setOnlineApp() = firebaseSave.saveOnlineApp(ONE)

    fun setOfflineApp() = firebaseSave.saveOnlineApp(ZERO)

    fun setNoWriting() = firebaseSave.saveWriting(ZERO)

    fun startListenerForCall() = firebaseRead.startListenerForCall()

    fun stopListenerForCall() = firebaseRead.stopListenerForCall()

    fun sendMessage(text: String) = firebaseSave.sendMessage(text, remoteMessages)

    fun deleteAllMessages() = firebaseSave.deleteAllMessages()

    fun call() = firebaseCall.call(remoteMessages)

    fun reject() = remoteMessages.reject()

    @Composable
    fun SetChatContent(content: @Composable FirebaseConnect.() -> Unit) {
        rememberUpdatedState(LocalLifecycleOwner.current).value.apply {
            DisposableEffect(this) {
                with(firebaseRead) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (Lifecycle.Event.ON_CREATE == event) initStates()
                        if (Lifecycle.Event.ON_RESUME == event) onResume()
                        if (Lifecycle.Event.ON_PAUSE == event) onPause()
                    }
                    lifecycle.addObserver(observer)
                    onDispose {
                        onPause()
                        lifecycle.removeObserver(observer)
                    }
                }
            }
            content(this@FirebaseConnect)
        }
    }

    private val firebaseSave by lazy {
        FirebaseSave(myDigit, this, timeConverter, myName, crypto)
    }

    private val timeConverter by lazy { TimeConverter() }

    private val crypto by lazy { Crypto(cryptoKey) }

    private val firebaseRead by lazy { FirebaseRead(firebaseSave) }

    private val remoteMessages by lazy { RemoteMessages(apiKey, firebaseRead) }

    private val firebaseCall by lazy { FirebaseCall(firebaseSave, firebaseRead) }

    companion object {
        const val ZERO = "0"
        const val ONE = "1"
    }
}

