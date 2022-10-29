package com.lm.firebaseconnectapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lm.firebaseconnect.UIUsersStates
import com.lm.firebaseconnect.UserModel
import com.lm.firebaseconnect.listUsers
import com.lm.firebaseconnectapp.firebaseConnect
import com.lm.firebaseconnectapp.ui.cells.CellCard
import com.lm.firebaseconnectapp.ui.cells.ChangePhoto
import com.lm.firebaseconnectapp.ui.cells.NameAndStatusBlockMainList

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Main(
    navController: NavHostController,
    onCallClick: (UserModel) -> Unit
) {
    firebaseConnect.SetMainScreenContent {
        if (listUsers.value is UIUsersStates.Success) {
            Column(Modifier.fillMaxSize()) {
                (listUsers.value as UIUsersStates.Success).list.forEach { model ->
                    CellCard(navController, model.id) {
                        ChangePhoto(
                            fullName = model.id,
                            photoUrl =
                            "https://qph.cf2.quoracdn.net/main-qimg-f92b2533456e0f5166866a4d463eb27d",
                            color = "-16524603",
                            onClick = {}, id = model.id) {
                            NameAndStatusBlockMainList(model, onCallClick)
                        }
                    }
                }
            }
        } else Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    }
}