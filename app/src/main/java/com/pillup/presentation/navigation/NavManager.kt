package com.pillup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pillup.presentation.views.LoginView
import com.pillup.presentation.views.RegisterView


@Composable
fun NavManager(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginView(navController)
        }

        composable("register") {
            RegisterView(navController)
        }

        // 🚧 Más pantallas se agregan después:
        // composable("home") { HomeView(navController) }
        // composable("profile") { ProfileView(navController) }
        // composable("settings") { SettingsView(navController) }
    }
}
