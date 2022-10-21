package com.lm.firebaseconnect

sealed class UIStates {
    object Loading : UIStates()
    class Success(val list: List<Pair<String, String>>) : UIStates()
}