package com.pillup.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pillup.presentation.viewmodel.LoginViewModel

@Composable
fun HomeView(loginViewModel: LoginViewModel) {

    val currentUser by loginViewModel.currentUser.collectAsState()

    Surface(color = Color(0xFF2563EB), modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Bienvenido",
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mostrar nombre y apellidos si están disponibles
            if (currentUser != null) {
                Text(
                    text = "${currentUser!!.nombre} ${currentUser!!.apellidos}",
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = currentUser!!.email,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            } else {
                Text(
                    text = "Cargando usuario...",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Aquí irán los medicamentos y otras vistas
            Text(
                text = "Medicamentos (próximamente)",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}