package com.pillup.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pillup.R
import com.pillup.presentation.viewmodel.ContactoEmergenciaState
import com.pillup.presentation.viewmodel.ContactoEmergenciaViewModel
import com.pillup.presentation.viewmodel.LoginViewModel
import com.pillup.presentation.viewmodel.MedicamentoViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeView(
    navController: NavController,
    loginViewModel: LoginViewModel,
    medicamentoViewModel: MedicamentoViewModel,
    contactoEmergenciaViewModel: ContactoEmergenciaViewModel
) {

    val context = LocalContext.current
    val contactoState by contactoEmergenciaViewModel.contactoState.collectAsState()
    val currentUser by loginViewModel.currentUser.collectAsState()
    val medicamentoState by medicamentoViewModel.medicamentoState.collectAsState()

    LaunchedEffect(Unit) {
        contactoEmergenciaViewModel.obtenerContactoEmergencia()
        medicamentoViewModel.obtenerMedicamentos()
        loginViewModel.cargarUsuarioActual()
    }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(Manifest.permission.SEND_SMS)
    }

    fun enviarSMS() {
        if (contactoState is ContactoEmergenciaState.Success) {
            val contacto = (contactoState as ContactoEmergenciaState.Success).contacto
            val numero = contacto.telefono
            val mensaje = "¡Ayuda! Necesito asistencia urgente. (Enviado desde PillUp)"

            if (numero.isNotBlank()) {
                try {
                    val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        context.getSystemService(SmsManager::class.java) ?: SmsManager.getDefault()
                    } else {
                        SmsManager.getDefault()
                    }

                    if (smsManager != null) {
                        smsManager.sendTextMessage(numero, null, mensaje, null, null)
                        Toast.makeText(context, "Alerta enviada a ${contacto.nombre}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Error: No se pudo acceder al servicio de SMS", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Fallo al enviar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(context, "El contacto no tiene número válido", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Primero configura un contacto de emergencia", Toast.LENGTH_LONG).show()
            navController.navigate("formulario_emergencia")
        }
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false

        if (smsGranted) {
            enviarSMS()
        } else {
            Toast.makeText(context, "Se requiere permiso SMS para enviar alertas de auxilio", Toast.LENGTH_LONG).show()
        }
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

            val nombreMostrar = currentUser?.nombre ?: "Usuario"

            Text(
                text = "Bienvenido $nombreMostrar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿Necesitas ayuda?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp)
            )

            Text(
                text = "Envía un mensaje a tú contacto de emergencia",
                fontSize = 15.sp,
                color = Color(0xFF02316E),
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp, bottom = 12.dp)
            )

            Button(
                onClick = {
                    val allGranted = permissionsToRequest.all { permission ->
                        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    }

                    if (allGranted) {
                        enviarSMS()
                    } else {
                        multiplePermissionLauncher.launch(permissionsToRequest)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Llamada de Auxilio", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tús medicamentos pendientes:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (medicamentoState) {
                is com.pillup.presentation.viewmodel.MedicamentoState.Success -> {
                    val medicamentos = (medicamentoState as com.pillup.presentation.viewmodel.MedicamentoState.Success).medicamentos

                    if (medicamentos.isEmpty()) {
                        Text(
                            text = "No hay medicamentos registrados",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(20.dp)
                        )
                    } else {
                        val lazyListState = rememberLazyListState()
                        val coroutineScope = rememberCoroutineScope()

                        val canGoBack by remember {
                            derivedStateOf { lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0 }
                        }
                        val canGoForward by remember {
                            derivedStateOf { lazyListState.firstVisibleItemIndex < medicamentos.size - 1 }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val current = lazyListState.firstVisibleItemIndex
                                    val offset = lazyListState.firstVisibleItemScrollOffset
                                    coroutineScope.launch {
                                        if (offset > 0) lazyListState.animateScrollToItem(current)
                                        else if (current > 0) lazyListState.animateScrollToItem(current - 1)
                                    }
                                },
                                enabled = canGoBack,
                                modifier = Modifier
                                    .background(if (canGoBack) Color(0xFF02316E) else Color.LightGray, RoundedCornerShape(8.dp))
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, "Anterior", tint = Color.White)
                            }

                            LazyRow(
                                state = lazyListState,
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(medicamentos) { med ->
                                    MedicamentoCardCarrusel(
                                        medicamento = med,
                                        onCardClick = { navController.navigate("detalle_medicamento/${med.id}") }
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    val current = lazyListState.firstVisibleItemIndex
                                    coroutineScope.launch {
                                        if (current < medicamentos.size - 1) lazyListState.animateScrollToItem(current + 1)
                                    }
                                },
                                enabled = canGoForward,
                                modifier = Modifier
                                    .background(if (canGoForward) Color(0xFF02316E) else Color.LightGray, RoundedCornerShape(8.dp))
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, "Siguiente", tint = Color.White)
                            }
                        }
                    }
                }
                is com.pillup.presentation.viewmodel.MedicamentoState.Error -> {
                    Text("Error al cargar medicamentos", color = Color.Red, modifier = Modifier.padding(20.dp))
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.padding(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("registrar_medicamento") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrar nuevo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.navigate("ver_todos_medicamentos") },
                    modifier = Modifier.weight(1f).height(50.dp).border(2.dp, Color(0xFF02316E), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver todos", color = Color(0xFF02316E), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configura a tú contacto de emergencia",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier.fillMaxWidth(0.9f).padding(start = 16.dp)
            )

            Text(
                text = "Tú contacto de emergencia te servirá en dado caso necesites alguna ayuda de manera urgente.",
                fontSize = 14.sp,
                color = Color(0xFF02316E),
                modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Button(
                onClick = { navController.navigate("formulario_emergencia") },
                modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contacto de emergencia", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).background(Color(0xFF1877F2), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF1877F2)).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_dialog_info), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Text("No te olvides de mantener actualizado cada parte de tú boticaín virtual", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        BottomNavigationBar(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
@Composable
fun MedicamentoCardCarrusel(
    medicamento: com.pillup.data.model.Medicamento,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = medicamento.nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${medicamento.dosis} mg",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (medicamento.fotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = File(medicamento.fotoUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Siguiente toma en:",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = medicamento.proximaToma,
                fontSize = 18.sp,
                color = Color(0xFF1877F2),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onCardClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver foto", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Home,
                contentDescription = "Home",
                tint = Color(0xFF1877F2),
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = { navController.navigate("registrar_medicamento") }) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = { navController.navigate("contacto_emergencia") }) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Perfil",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}