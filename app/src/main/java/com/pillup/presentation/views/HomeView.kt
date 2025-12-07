package com.pillup.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pillup.R
import com.pillup.presentation.components.BottomNavigation
import com.pillup.presentation.components.MedicamentoCard
import com.pillup.presentation.viewmodel.LoginViewModel
import com.pillup.presentation.viewmodel.MedicamentoViewModel

@Composable
fun HomeView(
    navController: NavController,
    loginViewModel: LoginViewModel,
    medicamentoViewModel: MedicamentoViewModel
) {

    val currentUser by loginViewModel.currentUser.collectAsState()
    val medicamentoState by medicamentoViewModel.medicamentoState.collectAsState()

    // Cargar medicamentos al entrar
    LaunchedEffect(Unit) {
        medicamentoViewModel.obtenerMedicamentos()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.back1),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.90f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Saludo
            if (currentUser != null) {
                Text(
                    text = "Bienvenido ${currentUser!!.nombre}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón llamada de auxilio
            Button(
                onClick = { navController.navigate("formulario_emergencia") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Llamada de Auxilio", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Medicamentos pendientes
            Text(
                text = "Tús medicamentos pendientes:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de medicamentos
            when (medicamentoState) {
                is com.pillup.presentation.viewmodel.MedicamentoState.Success -> {
                    val medicamentos = (medicamentoState as com.pillup.presentation.viewmodel.MedicamentoState.Success).medicamentos

                    if (medicamentos.isEmpty()) {
                        Text(
                            text = "No hay medicamentos registrados",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(20.dp)
                        )
                    } else {
                        medicamentos.forEach { med ->
                            MedicamentoCard(
                                medicamento = med,
                                onCardClick = {
                                    navController.navigate("detalle_medicamento/${med.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                is com.pillup.presentation.viewmodel.MedicamentoState.Error -> {
                    Text(
                        text = "Error al cargar medicamentos",
                        color = Color.Red,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.padding(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("registrar_medicamento") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrar nuevo", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = { navController.navigate("ver_todos_medicamentos") },
                    modifier = Modifier.border(2.dp, Color(0xFF2563EB), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver todos", color = Color(0xFF2563EB), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Configurar contacto emergencia
            Text(
                text = "¿Configura a tú contacto de emergencia?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Text(
                text = "Tú contacto de emergencia te servirá en dado caso necesites alguna ayuda de manera urgente.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Button(
                onClick = { navController.navigate("formulario_emergencia") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contacto de emergencia", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notificación
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFF2196F3), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info),
                        contentDescription = "Notificación",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "No te olvides de mantener actualizado cada parte de tú boticaín virtual",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Bottom Navigation
        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}