package com.pillup.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeView() {

    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.email ?: "Usuario"

    Surface(color = Color(0xFF2563EB), modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Bienvenido",
                fontSize = 36.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = userName,
                fontSize = 22.sp,
                color = Color.White
            )
        }
    }
}
