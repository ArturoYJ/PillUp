package com.pillup.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
                    text = "Bienvenido ${currentUser?.nombre ?: "Usuario"}",                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sección ¿Necesitas ayuda?
            Text(
                text = "¿Necesitas ayuda?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Text(
                text = "Envía un mensaje a tú contacto de emergencia",
                fontSize = 15.sp,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp, bottom = 12.dp)
            )

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

            Spacer(modifier = Modifier.height(24.dp))

            // Medicamentos pendientes - Título
            Text(
                text = "Tús medicamentos pendientes:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de medicamentos - Carrusel horizontal
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón izquierda
                            IconButton(
                                onClick = {
                                    // Scroll izquierda
                                },
                                modifier = Modifier
                                    .background(Color(0xFF02316E), RoundedCornerShape(8.dp))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Anterior",
                                    tint = Color.White
                                )
                            }

                            // Carrusel
                            LazyRow(
                                state = lazyListState,
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(medicamentos) { med ->
                                    MedicamentoCardCarrusel(
                                        medicamento = med,
                                        onCardClick = {
                                            navController.navigate("detalle_medicamento/${med.id}")
                                        }
                                    )
                                }
                            }

                            // Botón derecha
                            IconButton(
                                onClick = {
                                    // Scroll derecha
                                },
                                modifier = Modifier
                                    .background(Color(0xFF02316E), RoundedCornerShape(8.dp))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Siguiente",
                                    tint = Color.White
                                )
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

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("registrar_medicamento") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrar nuevo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.navigate("ver_todos_medicamentos") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(2.dp, Color(0xFF02316E), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver todos", color = Color(0xFF02316E), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Configurar contacto emergencia - Título
            Text(
                text = "Configura a tú contacto de emergencia",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 16.dp)
            )

            Text(
                text = "Tú contacto de emergencia te servirá en dado caso necesites alguna ayuda de manera urgente.",
                fontSize = 14.sp,
                color = Color(0xFF02316E),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Button(
                onClick = { navController.navigate("formulario_emergencia") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02316E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contacto de emergencia", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notificación - Fondo azul claro con texto blanco
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFF1877F2), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1877F2))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info),
                        contentDescription = "Notificación",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "No te olvides de mantener actualizado cada parte de tú boticaín virtual",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Bottom Navigation
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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

            // Botón Ver foto
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
        // Home
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Home,
                contentDescription = "Home",
                tint = Color(0xFF1877F2),
                modifier = Modifier.size(28.dp)
            )
        }

        // Agregar medicamento
        IconButton(onClick = { navController.navigate("registrar_medicamento") }) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        // Perfil / Contacto de emergencia
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