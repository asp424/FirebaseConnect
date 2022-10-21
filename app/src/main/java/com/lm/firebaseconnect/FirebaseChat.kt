package com.lm.firebaseconnect

class FirebaseChat {
    private var cryptoKey: String = "1111111111111111"

    private var myName: String = ""

    private var myDigit: String = "3564"

    private var himDigit: String = "7452"

    private var databaseUserId: String = ""

    private var apiKey: String = ""

    private var token: String = ""

    fun setMyDigit(myDigit: String) = apply { this.myDigit = myDigit }

    fun setToken(token: String) = apply { this.token = token }

    fun setApiKey(apiKey: String) = apply { this.apiKey = apiKey }

    fun setDatabaseUserId(databaseUserId: String) = apply { this.databaseUserId = databaseUserId }

    fun setHimDigit(himDigit: String) = apply { this.himDigit = himDigit }

    fun setCryptoKey(cryptoKey: String) = apply { this.cryptoKey = cryptoKey }

    fun setMyName(myName: () -> String) = apply { this.myName = myName.invoke() }

    fun setOnline() {
        firebaseSave.saveOnline(ONE)
        firebaseSave.clearHimNotify()
    }

    fun setOffline() = firebaseSave.saveOnline(ZERO)

    fun setNoWriting() = firebaseSave.saveWriting(ZERO)

    fun setWriting() = firebaseSave.saveWriting(ONE)

    fun sendMessage(text: String) = firebaseSave.sendMessage(text, fcmProvider)

    fun getAndSaveToken(onGet: (String) -> Unit) = fcmProvider.getAndSaveToken { onGet(it) }

    fun startListener() = firebaseRead.startListener()

    fun stopListener() = firebaseRead.stopListener()

    fun deleteAllMessages() = firebaseSave.deleteAllMessages()

    private val fcmProvider by lazy { FCMProvider(databaseUserId, apiKey, firebaseRead, token) }

    private val firebaseSave by lazy {
        FirebaseSave(myDigit, himDigit, timeConverter, myName, crypto)
    }

    private val timeConverter by lazy { TimeConverter() }

    private val crypto by lazy { Crypto(cryptoKey) }

    private val firebaseRead by lazy { FirebaseRead(firebaseSave) }

    val onLineState = firebaseRead.onLineState

    val writingState = firebaseRead.writingState

    val notifyState = firebaseRead.notifyState

    val listMessages = firebaseRead.listMessages

    companion object {
        private const val ZERO = "0"
        private const val ONE = "1"
    }
}

