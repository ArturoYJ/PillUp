package com.pillup.presentation.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import java.io.FileOutputStream
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pillup.data.model.Medicamento
import com.pillup.presentation.viewmodel.MedicamentoViewModel
import java.io.File
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
    var dosis by remember { mutableIntStateOf(1) }
    var primeraToma by remember { mutableStateOf("") }
    var intervalo by remember { mutableIntStateOf(8) }
    var duracion by remember { mutableStateOf("") }
    var importancia by remember { mutableStateOf("Media") }
    var instrucciones by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Bandera local para controlar cuándo estamos guardando
    var isSaving by remember { mutableStateOf(false) }

    // --- VARIABLES PARA CÁMARA (NUEVO) ---
    var fotoUri by remember { mutableStateOf<Uri?>(null) } // Foto final a mostrar/guardar
    var tempUri by remember { mutableStateOf<Uri?>(null) } // Uri temporal para la cámara
    val context = LocalContext.current

    // Lanzador de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            fotoUri = tempUri // Si tomó la foto, la guardamos en la variable principal
        }
    }

    // Función auxiliar para crear el archivo temporal
    fun crearArchivoTemporal(): Uri {
        val file = File(context.cacheDir, "foto_med_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Debe coincidir con AndroidManifest
            file
        )
    }

    // --- Lógica del Calendario ---
    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC") // Corrección de fecha
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

    // Reacción al guardado
    LaunchedEffect(medicamentoState) {
        if (isSaving) {
            when (medicamentoState) {
                is com.pillup.presentation.viewmodel.MedicamentoState.Success -> {
                    isSaving = false
                    viewModel.limpiarEstado()
                    // Navegar a la lista
                    navController.navigate("ver_todos_medicamentos") {
                        popUpTo("registrar_medicamento") { inclusive = true }
                    }
                }
                is com.pillup.presentation.viewmodel.MedicamentoState.Error -> {
                    isSaving = false
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color(0xFF02316E))
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

        // Duración
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
                readOnly = true,
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

        // --- SECCIÓN FOTO (ACTUALIZADA) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón para tomar foto
            OutlinedButton(
                onClick = {
                    tempUri = crearArchivoTemporal() // 1. Crear archivo
                    cameraLauncher.launch(tempUri!!) // 2. Lanzar cámara
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (fotoUri == null) "📷 Tomar Foto" else "📷 Cambiar Foto",
                    color = Color(0xFF02316E), fontSize = 14.sp)
            }

            // Previsualización (Miniatura)
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto medicamento",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                if (nombre.isBlank()) {
                    errorMessage = "El nombre del medicamento es requerido"
                } else {
                    isSaving = true

                    // 1. Lógica de Fecha (Próxima toma)
                    val proximaTomaCalculada = com.pillup.utils.TimeUtils.calcularProximaToma(
                        primeraToma = primeraToma,
                        intervaloHoras = intervalo
                    )

                    // 2. Lógica de Foto LOCAL
                    var rutaFotoLocal = ""
                    if (fotoUri != null) {
                        try {
                            // Creamos un archivo permanente en la carpeta de la app
                            val nombreArchivo = "img_${System.currentTimeMillis()}.jpg"
                            val archivoPermanente = File(context.filesDir, nombreArchivo)

                            // Copiamos los bytes de la foto temporal al archivo permanente
                            context.contentResolver.openInputStream(fotoUri!!)?.use { input ->
                                FileOutputStream(archivoPermanente).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            // Guardamos la ruta absoluta (ej: /data/user/0/com.pillup/files/img_123.jpg)
                            rutaFotoLocal = archivoPermanente.absolutePath
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // 3. Crear objeto (ahora incluimos la fotoUrl aquí directo)
                    val medicamento = Medicamento(
                        nombre = nombre,
                        dosis = dosis,
                        primeraToma = primeraToma,
                        intervalo = intervalo,
                        duracion = duracion,
                        importancia = importancia,
                        instrucciones = instrucciones,
                        proximaToma = proximaTomaCalculada,
                        fotoUrl = rutaFotoLocal // <--- Aquí va la ruta local
                    )

                    // 4. Llamar al ViewModel (ya no pide uri, solo el objeto)
                    viewModel.crearMedicamento(medicamento)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
            shape = RoundedCornerShape(12.dp),
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