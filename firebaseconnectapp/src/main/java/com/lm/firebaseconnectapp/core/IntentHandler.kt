package com.lm.firebaseconnectapp.core

import android.content.Intent
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnect.States.ICON
import com.lm.firebaseconnect.States.NAME
import com.lm.firebaseconnect.States.TOKEN
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getNavState
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setUserModelChat
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import javax.inject.Inject

interface IntentHandler {

    fun onCreate(intent: Intent, onValidIntent: () -> Unit, onErrorIntent: () -> Unit)

    fun onNewIntent(intent: Intent)

    class Base @Inject constructor(
        private val sPreferences: SPreferences,
        private val fBConnect: FirebaseConnect
    ) : IntentHandler {

        override fun onCreate(intent: Intent, onValidIntent: () -> Unit, onErrorIntent: () -> Unit)
        {
            if (intent.action == States.MESSAGE) {
                sPreferences.saveChatUserModel(intent.getUserModel()); onValidIntent()
            } else onErrorIntent()
        }

        override fun onNewIntent(intent: Intent) {
            if (intent.action == States.MESSAGE) {
                val model = intent.getUserModel()
                    sPreferences.saveChatUserModel(model.apply { setUserModelChat(model) })
                    if (getNavState == NavRoutes.MAIN || getNavState == NavRoutes.EMPTY)
                        setNavState(NavRoutes.CHAT)
                    else {
                        with(fBConnect) {
                            setChatId(model.id.toInt())
                            listMessages.value = UIMessagesStates.Loading
                            firebaseRead.startListener()
                            firebaseHandler.startMainListener()
                        }
                }
            }
        }
    }
}

private fun Intent.getValue(key: String) = getStringExtra(key)

private fun Intent.getUserModel() = UserModel(
    id = getValue(CALLING_ID) ?: "",
    name = getValue(NAME) ?: "",
    iconUri = getValue(ICON) ?: "",
    token = getValue(TOKEN) ?: "",
)
