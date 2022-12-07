package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import kotlinx.coroutines.launch

@Composable
fun ToBottomButton(listState: LazyListState, key: String, size: Int) =
    Box(
        Modifier
            .fillMaxSize()
            .padding(end = 60.dp, bottom = 80.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        val coroutine = rememberCoroutineScope()
        val isLastItem by remember(size) {
            derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key == key }
        }

        val click = remember { { coroutine.launch { listState.scrollToItem(size) }; Unit } }

        FloatingActionButton(
            click, Modifier.scale(
                animateFloatAsState(if (!listState.isScrollingUp() && !isLastItem) 0.9f else 0f)
                    .value
            ), CircleShape, getMainColor
        ) { Icon(Icons.Default.ArrowDownward, null, tint = getSecondColor) }
    }

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            (if (previousIndex != firstVisibleItemIndex) previousIndex > firstVisibleItemIndex
            else previousScrollOffset >= firstVisibleItemScrollOffset
                    ).also {
                    previousIndex = firstVisibleItemIndex
                    previousScrollOffset = firstVisibleItemScrollOffset
                }
        }
    }.value
}