package com.poetry.shijian.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.poetry.shijian.data.repository.PoetryRepository
import com.poetry.shijian.ui.daily.DailyScreen
import com.poetry.shijian.ui.detail.PoemDetailScreen
import com.poetry.shijian.ui.discover.DiscoverScreen
import com.poetry.shijian.ui.library.LibraryScreen
import com.poetry.shijian.ui.profile.FavoritesScreen
import com.poetry.shijian.ui.profile.HistoryScreen
import com.poetry.shijian.ui.profile.ProfileScreen
import com.poetry.shijian.ui.profile.SettingsScreen

@Composable
fun ShijianNavGraph(
    navController: NavHostController,
    repository: PoetryRepository,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Daily.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(350),
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(350),
            ) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(350),
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(350),
            ) + fadeOut(animationSpec = tween(200))
        },
    ) {
        composable("daily") {
            DailyScreen(repository = repository)
        }
        composable("library") {
            LibraryScreen(
                repository = repository,
                onPoemClick = { poemId ->
                    navController.navigate("poem/$poemId")
                },
            )
        }
        composable("discover") {
            DiscoverScreen(
                repository = repository,
                onPoemClick = { poemId ->
                    navController.navigate("poem/$poemId")
                },
            )
        }
        composable("profile") {
            ProfileScreen(
                repository = repository,
                onFavoritesClick = { navController.navigate("favorites") },
                onHistoryClick = { navController.navigate("history") },
                onSettingsClick = { navController.navigate("settings") },
            )
        }
        composable("poem/{poemId}") { backStackEntry ->
            val poemId = backStackEntry.arguments?.getString("poemId")?.toLongOrNull() ?: return@composable
            PoemDetailScreen(
                poemId = poemId,
                repository = repository,
                onBack = { navController.popBackStack() },
            )
        }
        composable("favorites") {
            FavoritesScreen(
                repository = repository,
                onPoemClick = { poemId -> navController.navigate("poem/$poemId") },
                onBack = { navController.popBackStack() },
            )
        }
        composable("history") {
            HistoryScreen(
                repository = repository,
                onPoemClick = { poemId -> navController.navigate("poem/$poemId") },
                onBack = { navController.popBackStack() },
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
