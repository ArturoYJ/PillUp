package com.pillup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pillup.presentation.view.*
import com.pillup.presentation.viewmodel.LoginViewModel
import com.pillup.presentation.viewmodel.RegisterViewModel

@Composable
fun NavManager(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashView(navController)
        }

        composable("login") {
            LoginView(navController, LoginViewModel())
        }

        composable("register") {
            RegisterView(navController, RegisterViewModel())
        }

        composable("home") {
            HomeView()
        }
    }
}
