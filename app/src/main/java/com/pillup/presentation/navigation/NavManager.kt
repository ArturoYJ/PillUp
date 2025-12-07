package com.pillup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pillup.presentation.view.*
import com.pillup.presentation.viewmodel.LoginViewModel
import com.pillup.presentation.viewmodel.RegisterViewModel
import com.pillup.presentation.viewmodel.MedicamentoViewModel

@Composable
fun NavManager(navController: NavHostController) {

    // ViewModels compartidos entre pantallas
    val loginViewModel = LoginViewModel()
    val medicamentoViewModel = MedicamentoViewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashView(navController)
        }

        composable("login") {
            LoginView(navController, loginViewModel)
        }

        composable("register") {
            RegisterView(navController, RegisterViewModel())
        }

        composable("home") {
            HomeView(navController, loginViewModel, medicamentoViewModel)
        }

        composable("registrar_medicamento") {
            RegistrarMedicamentoView(navController, medicamentoViewModel)
        }

        composable("ver_todos_medicamentos") {
            VerTodosMedicamentosView(navController, medicamentoViewModel)
        }

        composable("detalle_medicamento/{medicamentoId}") { backStackEntry ->
            val medicamentoId = backStackEntry.arguments?.getString("medicamentoId") ?: ""
            DetalleMedicamentoView(navController, medicamentoViewModel, medicamentoId)
        }
    }
}