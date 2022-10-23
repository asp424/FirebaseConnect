package com.lm.firebaseconnect

sealed interface UIStates {
    object Loading : UIStates
    class Success(val list: List<Pair<String, String>>) : UIStates
}