package com.android.lenx.services

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.lenx.screens.HomeScreen
import com.android.lenx.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigator(isConnected: Boolean, exposePort: (String) -> Unit, disconnect: () -> Unit, batteryPercentage: Int) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                routeSettings = { navController.navigate(Screen.Settings.route) },
                exposePort = exposePort,
                isConnected = isConnected,
                disconnect = disconnect,
                batteryPercentage = batteryPercentage
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                connected = isConnected
            )
        }
    }
}
