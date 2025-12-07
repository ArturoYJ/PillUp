package com.pillup.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pillup.presentation.viewmodel.MedicamentoViewModel

@Composable
fun VerTodosMedicamentosView(
    navController: NavController,
    viewModel: MedicamentoViewModel
) {

    val medicamentoState by viewModel.medicamentoState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerMedicamentos()
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
            Text(
                text = "Tús Medicamentos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (medicamentoState) {
            is com.pillup.presentation.viewmodel.MedicamentoState.Success -> {
                val medicamentos = (medicamentoState as com.pillup.presentation.viewmodel.MedicamentoState.Success).medicamentos

                if (medicamentos.isEmpty()) {
                    Text(
                        text = "No hay medicamentos registrados",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    medicamentos.forEach { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = med.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF02316E)
                                )
                                Text(
                                    "${med.dosis} Dosis",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "Siguiente toma en: ${med.proximaToma}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1877F2)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { navController.navigate("detalle_medicamento/${med.id}") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Entrar", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
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

        Spacer(modifier = Modifier.height(40.dp))
    }
}