package com.lm.firebaseconnect

sealed interface UIMessagesStates {
    object Loading : UIMessagesStates
    class Success(val list: List<Pair<String, String>>) : UIMessagesStates
}

sealed interface UIUsersStates {
    object Loading : UIUsersStates
    class Success(val list: List<UserModel>) : UIUsersStates
    class Update(val userModel: UserModel) : UIUsersStates
}