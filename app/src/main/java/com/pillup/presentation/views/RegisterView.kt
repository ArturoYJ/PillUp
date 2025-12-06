package com.pillup.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pillup.R
import com.pillup.presentation.viewmodel.RegisterState
import com.pillup.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterView(navController: NavController, viewModel: RegisterViewModel) {

    var names by remember { mutableStateOf("") }
    var lastNames by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02316E)
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = names,
                onValueChange = { names = it },
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastNames,
                onValueChange = { lastNames = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptTerms, onCheckedChange = { acceptTerms = it })
                Text("Acepto términos y condiciones")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (acceptTerms && names.isNotBlank() && lastNames.isNotBlank() &&
                        email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                        if (password == confirmPassword) {
                            viewModel.register(names, lastNames, email, password)
                        } else {
                            // Mostrar error de contraseñas no coinciden
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF033C86)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Registrarse", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = Color(0xFF02316E),
                modifier = Modifier.clickable {
                    navController.popBackStack()
                    navController.navigate("login")
                },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (registerState) {
                is RegisterState.Error -> Text(
                    text = (registerState as RegisterState.Error).message,
                    color = Color.Red
                )
                RegisterState.Loading -> CircularProgressIndicator()
                else -> {}
            }
        }
    }
}