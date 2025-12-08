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
import com.pillup.presentation.viewmodel.ContactoEmergenciaViewModel

@Composable
fun ContactoEmergenciaView(
    navController: NavController,
    viewModel: ContactoEmergenciaViewModel
) {

    val contactoState by viewModel.contactoState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerContactoEmergencia()
    }

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

        when (contactoState) {
            is com.pillup.presentation.viewmodel.ContactoEmergenciaState.Success -> {
                val contacto = (contactoState as com.pillup.presentation.viewmodel.ContactoEmergenciaState.Success).contacto

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
                        Text(contacto.nombre, fontSize = 16.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Teléfono:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF02316E),
                            fontSize = 12.sp
                        )
                        Text(contacto.telefono, fontSize = 16.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Relación:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF02316E),
                            fontSize = 12.sp
                        )
                        Text(contacto.relacion, fontSize = 16.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("formulario_emergencia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Editar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            is com.pillup.presentation.viewmodel.ContactoEmergenciaState.NoExiste -> {
                Text(
                    text = "No tienes un contacto de emergencia configurado",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("formulario_emergencia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Agregar contacto", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            is com.pillup.presentation.viewmodel.ContactoEmergenciaState.Error -> {
                Text(
                    text = "Error al cargar el contacto",
                    color = Color.Red,
                    modifier = Modifier.padding(20.dp)
                )
            }
            else -> {
                CircularProgressIndicator(modifier = Modifier.padding(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}