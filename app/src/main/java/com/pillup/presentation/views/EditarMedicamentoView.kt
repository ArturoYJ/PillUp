package com.pillup.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pillup.data.model.Medicamento
import com.pillup.presentation.viewmodel.MedicamentoDetailState
import com.pillup.presentation.viewmodel.MedicamentoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMedicamentoView(
    navController: NavController,
    viewModel: MedicamentoViewModel,
    medicamentoId: String
) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf(1) }
    var primeraToma by remember { mutableStateOf("") }
    var intervalo by remember { mutableStateOf(8) }
    var duracion by remember { mutableStateOf("") }
    var importancia by remember { mutableStateOf("Media") }
    var instrucciones by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var dataLoaded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val detailState by viewModel.medicamentoDetailState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerMedicamento(medicamentoId)
    }

    LaunchedEffect(detailState) {
        if (!dataLoaded && detailState is MedicamentoDetailState.Success) {
            val med = (detailState as MedicamentoDetailState.Success).medicamento
            if (med.id == medicamentoId) {
                nombre = med.nombre
                dosis = med.dosis
                primeraToma = med.primeraToma
                intervalo = med.intervalo
                duracion = med.duracion
                importancia = med.importancia
                instrucciones = med.instrucciones
                dataLoaded = true
            }
        }

        if (isSaving && detailState is MedicamentoDetailState.Success) {
            isSaving = false
            navController.popBackStack()
        }

        if (isSaving && detailState is MedicamentoDetailState.Error) {
            isSaving = false
            errorMessage = (detailState as MedicamentoDetailState.Error).message
        }
    }

    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    if (dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        val inicio = formatter.format(Date(dateRangePickerState.selectedStartDateMillis!!))
                        val fin = formatter.format(Date(dateRangePickerState.selectedEndDateMillis!!))
                        duracion = "$inicio - $fin"
                        showDateRangePicker = false
                    }
                }) { Text("Guardar", color = Color(0xFF02316E)) }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) { Text("Cancelar", color = Color.Red) }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, modifier = Modifier.height(400.dp))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            text = "Editar medicamento",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!dataLoaded && detailState is MedicamentoDetailState.Loading) {
            CircularProgressIndicator(color = Color(0xFF02316E))
        } else {
            Text("Nombre:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dosis:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(50.dp).clickable { /* No action needed container */ },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (dosis > 1) dosis-- }) {
                            Icon(Icons.Default.Add, "Menos", tint = Color(0xFF1877F2), modifier = Modifier.rotate(45f))
                        }
                        Text(dosis.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF02316E))
                        IconButton(onClick = { dosis++ }) {
                            Icon(Icons.Default.Add, "Mas", tint = Color(0xFF02316E))
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Primera toma:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
                    OutlinedTextField(
                        value = primeraToma, onValueChange = { primeraToma = it },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("HH:mm") }, shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Intervalo (horas):", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
            OutlinedTextField(
                value = intervalo.toString(),
                onValueChange = { intervalo = it.toIntOrNull() ?: 8 },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Duración:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = duracion, onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DateRange, null, tint = Color(0xFF02316E)) },
                    shape = RoundedCornerShape(8.dp)
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDateRangePicker = true })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Importancia:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Baja", "Media", "Alta").forEach { imp ->
                    Button(
                        onClick = { importancia = imp },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (importancia == imp) Color(0xFF02316E) else Color.White)
                    ) {
                        Text(imp, color = if (importancia == imp) Color.White else Color(0xFF02316E), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Instrucciones:", fontWeight = FontWeight.Bold, color = Color(0xFF02316E), fontSize = 12.sp)
            OutlinedTextField(
                value = instrucciones, onValueChange = { instrucciones = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMessage = "El nombre es requerido"
                    } else {
                        isSaving = true

                        val proximaTomaCalculada = com.pillup.utils.TimeUtils.calcularProximaToma(
                            primeraToma = primeraToma,
                            intervaloHoras = intervalo
                        )

                        val medicamentoActualizado = Medicamento(
                            id = medicamentoId,
                            nombre = nombre,
                            dosis = dosis,
                            primeraToma = primeraToma,
                            intervalo = intervalo,
                            duracion = duracion,
                            importancia = importancia,
                            instrucciones = instrucciones,
                            proximaToma = proximaTomaCalculada
                        )
                        viewModel.actualizarMedicamento(medicamentoId, medicamentoActualizado)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}