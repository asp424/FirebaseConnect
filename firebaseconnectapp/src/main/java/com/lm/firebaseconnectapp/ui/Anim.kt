package com.lm.firebaseconnectapp.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<NavBackStackEntry>.enterLeftToRight
    get() = slideIntoContainer(AnimatedContentScope.SlideDirection.Right, tween(350))

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<NavBackStackEntry>.enterRightToLeft
    get() = slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(350))

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<NavBackStackEntry>.exitLeftToRight
    get() = slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(350))

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<NavBackStackEntry>.exitRightToLeft
    get() = slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(350))
