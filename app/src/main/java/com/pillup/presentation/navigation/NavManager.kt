package com.pillup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pillup.presentation.view.*
import com.pillup.presentation.viewmodel.LoginViewModel
import com.pillup.presentation.viewmodel.RegisterViewModel
import com.pillup.presentation.viewmodel.MedicamentoViewModel
import com.pillup.presentation.viewmodel.ContactoEmergenciaViewModel
import com.pillup.presentation.views.EditarMedicamentoView

@Composable
fun NavManager(navController: NavHostController) {

    val loginViewModel: LoginViewModel = viewModel()
    val medicamentoViewModel: MedicamentoViewModel = viewModel()
    val contactoEmergenciaViewModel: ContactoEmergenciaViewModel = viewModel()

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
            // CORRECCIÓN 2: Usar viewModel() aquí también, no RegisterViewModel()
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterView(navController, registerViewModel)
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

        composable("editar_medicamento/{medicamentoId}") { backStackEntry ->
            val medicamentoId = backStackEntry.arguments?.getString("medicamentoId") ?: ""
            EditarMedicamentoView(navController, medicamentoViewModel, medicamentoId)
        }

        composable("contacto_emergencia") {
            ContactoEmergenciaView(navController, contactoEmergenciaViewModel)
        }

        composable("formulario_emergencia") {
            FormularioEmergenciaView(navController, contactoEmergenciaViewModel)
        }
    }
}