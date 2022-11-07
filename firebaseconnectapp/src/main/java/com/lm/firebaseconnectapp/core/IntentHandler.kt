package com.lm.firebaseconnectapp.core

import android.content.Intent
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.CALLING_ID
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.ui.UiStates.getNavState
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import javax.inject.Inject

interface IntentHandler {

    fun onCreate(intent: Intent, onValidIntent: () -> Unit, onEmptyIntent: () -> Unit)

    fun onNewIntent(intent: Intent)

    class Base @Inject constructor(
        private val sPreferences: SPreferences
    ) : IntentHandler {

        override fun onCreate(
            intent: Intent,
            onValidIntent: () -> Unit,
            onEmptyIntent: () -> Unit
        ) {
            if (intent.action == States.MESSAGE) {
                sPreferences.saveChatId(intent.getValue(CALLING_ID))
                onValidIntent()
            } else onEmptyIntent()
        }

        override fun onNewIntent(intent: Intent) {
            if (intent.action == States.MESSAGE) {
                sPreferences.saveChatId(intent.getValue(CALLING_ID))
                if (getNavState == NavRoutes.MAIN || getNavState == NavRoutes.EMPTY)
                    setNavState(NavRoutes.CHAT)
            }
        }
    }
}

private fun Intent.getValue(key: String) = getStringExtra(key) ?: ""


