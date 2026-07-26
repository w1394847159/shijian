package com.poetry.shijian.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/** 底部导航项定义 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Daily : BottomNavItem(
        route = "daily",
        label = "每日",
        icon = Icons.Outlined.WbSunny,
    )

    data object Library : BottomNavItem(
        route = "library",
        label = "文库",
        icon = Icons.Outlined.AutoStories,
    )

    data object Discover : BottomNavItem(
        route = "discover",
        label = "发现",
        icon = Icons.Outlined.Explore,
    )

    data object Profile : BottomNavItem(
        route = "profile",
        label = "我的",
        icon = Icons.Outlined.Person,
    )

    companion object {
        val items = listOf(Daily, Library, Discover, Profile)
    }
}
