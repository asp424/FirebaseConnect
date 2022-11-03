package com.lm.firebaseconnect.models

import com.lm.firebaseconnect.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class A {

    fun res() {
        CoroutineScope(IO).launch {
            (1..4).asFlow().onEach { it + 2 }.collect {
                (2 * it).log
            }
        }
    }
}
