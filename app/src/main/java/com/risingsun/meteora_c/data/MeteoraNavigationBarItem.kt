package com.risingsun.meteora_c.data

import androidx.compose.runtime.Composable

class MeteoraNavigationBarItem(
    val label: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val route: String
)