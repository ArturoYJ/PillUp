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
fun ContactoEmergenciaView(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val currentUser by loginViewModel.currentUser.collectAsState()

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
            text = "Tú contacto de emergencia",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Nombre:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text("Tu contacto de emergencia", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Teléfono:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text("+1 (234) 567-8900", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Relación:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text("Familiar", fontSize = 16.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("formulario_emergencia") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Editar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}