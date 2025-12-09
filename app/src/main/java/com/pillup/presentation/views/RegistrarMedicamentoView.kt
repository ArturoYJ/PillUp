package com.pillup.presentation.view

import androidx.compose.foundation.background
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
import com.pillup.presentation.viewmodel.MedicamentoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMedicamentoView(
    navController: NavController,
    viewModel: MedicamentoViewModel
) {

    // --- Variables del Formulario ---
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf(1) }
    var primeraToma by remember { mutableStateOf("") }
    var intervalo by remember { mutableStateOf(8) }
    var duracion by remember { mutableStateOf("") }
    var importancia by remember { mutableStateOf("Media") }
    var instrucciones by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Bandera local para controlar cuándo estamos guardando nosotros
    var isSaving by remember { mutableStateOf(false) }

    // --- Lógica del Calendario ---
    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return formatter.format(Date(millis))
    }

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    if (dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null) {
                        val inicio = convertMillisToDate(dateRangePickerState.selectedStartDateMillis!!)
                        val fin = convertMillisToDate(dateRangePickerState.selectedEndDateMillis!!)
                        duracion = "$inicio - $fin"
                        showDateRangePicker = false
                    }
                }) {
                    Text("Guardar", color = Color(0xFF02316E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancelar", color = Color.Red)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(400.dp),
                title = {
                    Text(
                        text = "Selecciona el rango del tratamiento",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    // --- Estado y Navegación ---
    val medicamentoState by viewModel.medicamentoState.collectAsState()

    // Limpieza de seguridad al entrar
    LaunchedEffect(Unit) {
        viewModel.limpiarEstado()
    }

    // Reacción al guardado (Solo si isSaving es true)
    LaunchedEffect(medicamentoState) {
        if (isSaving) { // IMPORTANTE: Solo reacciona si nosotros iniciamos el guardado
            when (medicamentoState) {
                is com.pillup.presentation.viewmodel.MedicamentoState.Success -> {
                    isSaving = false // Resetear bandera
                    viewModel.limpiarEstado()

                    // Navegar a la lista
                    navController.navigate("ver_todos_medicamentos") {
                        popUpTo("registrar_medicamento") { inclusive = true }
                    }
                }
                is com.pillup.presentation.viewmodel.MedicamentoState.Error -> {
                    isSaving = false // Resetear bandera para permitir reintentar
                    errorMessage = (medicamentoState as com.pillup.presentation.viewmodel.MedicamentoState.Error).message
                }
                else -> {}
            }
        }
    }

    // --- Interfaz ---
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
            text = "Registra tú medicamento",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre
        Text(
            text = "Agrega el nombre del medicamento:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Ej: Paracetamol") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dosis y Primera Toma
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tú Dosis:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { if (dosis > 1) dosis-- }, modifier = Modifier.weight(0.3f)) {
                        Icon(Icons.Default.Add, contentDescription = "Restar", tint = Color(0xFF1877F2), modifier = Modifier.rotate(45f))
                    }
                    Text(
                        text = dosis.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.4f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color(0xFF02316E)
                    )
                    IconButton(onClick = { dosis++ }, modifier = Modifier.weight(0.3f)) {
                        Icon(Icons.Default.Add, contentDescription = "Sumar", tint = Color(0xFF02316E))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Primera toma:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E)
                )
                OutlinedTextField(
                    value = primeraToma,
                    onValueChange = { primeraToma = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("HH:mm") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intervalo
        Text(
            text = "Intervalo de tú toma:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = intervalo.toString(),
                onValueChange = { intervalo = it.toIntOrNull() ?: 8 },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Horas", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duración (Campo Mejorado con Box Clickable)
        Text(
            text = "Duración del tratamiento:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = duracion,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Solo lectura visual
                placeholder = { Text("Seleccionar fechas") },
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Calendario",
                        tint = Color(0xFF02316E)
                    )
                }
            )
            // Box invisible que captura el clic con seguridad
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDateRangePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Importancia
        Text(
            text = "Importancia de tú tratamiento:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Baja", "Media", "Alta").forEach { imp ->
                Button(
                    onClick = { importancia = imp },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (importancia == imp) Color(0xFF02316E) else Color.White
                    )
                ) {
                    Text(
                        imp,
                        color = if (importancia == imp) Color.White else Color(0xFF02316E),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instrucciones
        Text(
            text = "Instrucciones:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF02316E)
        )
        OutlinedTextField(
            value = instrucciones,
            onValueChange = { instrucciones = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = { Text("Ej: Tomar después de cada comida") },
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Foto
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("📷 Foto", color = Color(0xFF02316E), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botón Registrar
        Button(
            onClick = {
                if (nombre.isBlank()) {
                    errorMessage = "El nombre del medicamento es requerido"
                } else {
                    isSaving = true // Activamos la bandera: "Estamos guardando"

                    val proximaTomaCalculada = com.pillup.utils.TimeUtils.calcularProximaToma(
                        primeraToma = primeraToma,
                        intervaloHoras = intervalo
                    )

                    val medicamento = Medicamento(
                        nombre = nombre,
                        dosis = dosis,
                        primeraToma = primeraToma,
                        intervalo = intervalo,
                        duracion = duracion,
                        importancia = importancia,
                        instrucciones = instrucciones,
                        proximaToma = proximaTomaCalculada
                    )
                    viewModel.crearMedicamento(medicamento)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
            shape = RoundedCornerShape(12.dp),
            // Deshabilitar botón mientras carga para evitar doble clic
            enabled = medicamentoState != com.pillup.presentation.viewmodel.MedicamentoState.Loading
        ) {
            if (medicamentoState == com.pillup.presentation.viewmodel.MedicamentoState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Registrar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}