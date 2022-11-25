package com.lm.firebaseconnectapp.ui.cells

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lm.firebaseconnectapp.R

@Composable
fun SetImage(
    photoUrl: String,
    sizeFlag: Boolean = true,
    onClick1: () -> Unit = {}
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    Glide.with(LocalContext.current).asBitmap()
        .load(photoUrl)
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmap = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                R.drawable.logo
            }
        })
    if (bitmap != null) {
        Box {
            Image(
                bitmap = bitmap!!.asImageBitmap(), modifier = with(
                    if (sizeFlag)
                        Modifier.size(42.dp)
                    else Modifier.size(120.dp)
                ) { clickable { onClick1() } }.clip(CircleShape), contentDescription = null
            )
        }
    } else {
        Box {
            Image(
                painter =
                painterResource(
                    id = if (isSystemInDarkTheme()) R.drawable.ic_baseline_person_24_light
                    else R.drawable.ic_baseline_person_24
                ), modifier = with(
                    if (sizeFlag)
                        Modifier.size(42.dp)
                    else Modifier.size(120.dp)
                ) { clickable { onClick1() } }.clip(CircleShape), contentDescription = null
            )
        }
    }
}



