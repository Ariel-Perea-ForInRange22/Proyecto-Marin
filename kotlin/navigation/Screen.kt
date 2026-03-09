package com.devcore.uat.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Pending : Screen("pending")
    object Home : Screen("home")
    object BusTracking : Screen("bus_tracking")
    object Profile : Screen("profile")
    object Marketplace : Screen("marketplace")
}
