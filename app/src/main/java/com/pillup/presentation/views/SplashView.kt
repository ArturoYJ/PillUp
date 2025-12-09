package com.pillup.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.pillup.R
import kotlinx.coroutines.delay

@Composable
fun SplashView(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(1500) // 1.5 segundos

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.back1),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Text("PillUp", fontSize = 28.sp)
        }
    }
}
