package com.lm.firebaseconnectapp.ui.cells.chat.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun OnIdAction(id: String) = with(mainDep.firebaseConnect) {
        LaunchedEffect(id) {
            withContext(Dispatchers.IO) {
                delay(700)
                firebaseRead.startListener()
            }
        }
    }
