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
import com.pillup.presentation.viewmodel.MedicamentoViewModel

@Composable
fun DetalleMedicamentoView(
    navController: NavController,
    viewModel: MedicamentoViewModel,
    medicamentoId: String
) {

    val medicamentoDetailState by viewModel.medicamentoDetailState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerMedicamento(medicamentoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF02316E))
            }
        }

        when (medicamentoDetailState) {
            is com.pillup.presentation.viewmodel.MedicamentoDetailState.Success -> {
                val med = (medicamentoDetailState as com.pillup.presentation.viewmodel.MedicamentoDetailState.Success).medicamento

                Text(
                    text = "Tú medicamento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Nombre del medicamento:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 14.sp
                )
                Text(med.nombre, fontSize = 18.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tú Dosis:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF02316E),
                            fontSize = 12.sp
                        )
                        Text("${med.dosis} Dosis", fontSize = 16.sp, color = Color.Gray)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Primera toma:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF02316E),
                            fontSize = 12.sp
                        )
                        Text(med.primeraToma, fontSize = 16.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Intervalo de tú toma:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text(
                    "Cada ${med.intervalo} ${med.intervaloUnidad}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Duración del tratamiento:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text(med.duracion, fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Importancia de tú tratamiento:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text("Es muy importante", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Instrucciones:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    fontSize = 12.sp
                )
                Text(med.instrucciones, fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("editar_medicamento/$medicamentoId") }, // Navega a la nueva vista
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Editar", color = Color(0xFF02316E), fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.eliminarMedicamento(medicamentoId)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Eliminar", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
            is com.pillup.presentation.viewmodel.MedicamentoDetailState.Error -> {
                Text(
                    text = "Error al cargar el medicamento",
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