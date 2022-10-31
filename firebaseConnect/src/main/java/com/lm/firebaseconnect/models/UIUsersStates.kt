package com.lm.firebaseconnect.models

sealed interface UIUsersStates {
    object Loading : UIUsersStates
    class Success(val list: List<UserModel>) : UIUsersStates
    class Update(val userModel: UserModel) : UIUsersStates
}