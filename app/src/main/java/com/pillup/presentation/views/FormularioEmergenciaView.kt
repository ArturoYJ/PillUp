package com.pillup.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pillup.presentation.viewmodel.LoginViewModel

@Composable
fun FormularioEmergenciaView(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var relacion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF02316E))
            }
        }

        Text(
            text = "Configura tú contacto de emergencia",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Nombre del contacto:",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("Ej: Juan Pérez") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Teléfono:",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("Ej: +1 (234) 567-8900") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Relación:",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = relacion,
            onValueChange = {
                relacion = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("Ej: Madre, Hermano, Amigo") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                if (nombre.isBlank()) {
                    errorMessage = "El nombre del contacto es requerido"
                } else if (telefono.isBlank()) {
                    errorMessage = "El teléfono es requerido"
                } else if (relacion.isBlank()) {
                    errorMessage = "La relación es requerida"
                } else {
                    // Aquí irá la lógica de guardar el contacto
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Guardar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}