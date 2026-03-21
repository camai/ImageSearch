package com.jg.imagesearch.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jg.imagesearch.feature.bookmark.BookmarkScreen
import com.jg.imagesearch.feature.search.SearchScreen
import com.jg.imagesearch.feature.viewer.ViewerScreen
import com.jg.imagesearch.core.model.ImageItem
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import com.jg.imagesearch.R

sealed class Screen(val route: String, val titleResId: Int) {
    data object Search : Screen("search", R.string.nav_search)
    data object Bookmark : Screen("bookmark", R.string.nav_bookmark)
    data object Viewer : Screen("viewer/{imageItemJson}", R.string.nav_viewer) {
        fun createRoute(item: ImageItem): String {
            val json = Gson().toJson(item)
            val encodedConfig = URLEncoder.encode(json, "UTF-8")
            return "viewer/$encodedConfig"
        }
    }
}

val BottomNavItems = listOf(Screen.Search, Screen.Bookmark)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute?.startsWith("viewer") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (screen == Screen.Search) Icons.Default.Search else Icons.Default.Favorite,
                                    contentDescription = stringResource(id = screen.titleResId)
                                )
                            },
                            label = { Text(stringResource(id = screen.titleResId)) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateToViewer = { item ->
                        navController.navigate(Screen.Viewer.createRoute(item))
                    }
                )
            }
            composable(Screen.Bookmark.route) {
                BookmarkScreen(
                    onNavigateToViewer = { item ->
                        navController.navigate(Screen.Viewer.createRoute(item))
                    }
                )
            }
            composable(
                route = Screen.Viewer.route,
                arguments = listOf(navArgument("imageItemJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedJson = backStackEntry.arguments?.getString("imageItemJson") ?: ""
                val decodedJson = URLDecoder.decode(encodedJson, "UTF-8")
                val item = runCatching {
                    Gson().fromJson(decodedJson, ImageItem::class.java)
                }.getOrNull()

                if (item != null) {
                    ViewerScreen(
                        selectedItem = item,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
