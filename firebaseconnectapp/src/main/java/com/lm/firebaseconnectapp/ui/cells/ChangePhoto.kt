package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChangePhoto(
    fullName: String,
    photoUrl: String,
    color: String,
    id: String,
    onClick: () -> Unit,
    inRow: @Composable (RowScope) -> Unit
) {
    Row(
        Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (photoUrl.isEmpty() && fullName.isNotEmpty()) {
            DrawCircle(fullName, color) { onClick() }
        } else SetImage(photoUrl) { onClick() }
        inRow(this)
    }
}