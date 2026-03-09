package com.devcore.uat.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devcore.uat.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Pending.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        
        composable(Screen.Pending.route) {
            PendingScreen(
                onBackClick = { navController.navigate(Screen.Login.route) }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onBusTrackingClick = { navController.navigate(Screen.BusTracking.route) },
                onMarketplaceClick = { navController.navigate(Screen.Marketplace.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
        
        composable(Screen.BusTracking.route) {
            BusTrackingScreen(
                onBackClick = { navController.popBackStack() },
                selectedTab = 1,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when(tab) {
                        0 -> navController.navigate(Screen.Home.route)
                        2 -> navController.navigate(Screen.Marketplace.route)
                        3 -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                selectedTab = 3,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when(tab) {
                        0 -> navController.navigate(Screen.Home.route)
                        1 -> navController.navigate(Screen.BusTracking.route)
                        2 -> navController.navigate(Screen.Marketplace.route)
                    }
                }
            )
        }
        
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(
                onBackClick = { navController.popBackStack() },
                selectedTab = 2,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when(tab) {
                        0 -> navController.navigate(Screen.Home.route)
                        1 -> navController.navigate(Screen.BusTracking.route)
                        3 -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
    }
}
